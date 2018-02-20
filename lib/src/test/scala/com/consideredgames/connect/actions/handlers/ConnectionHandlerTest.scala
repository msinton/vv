package com.consideredgames.connect.actions.handlers

import com.consideredgames.common.DateUtils
import com.consideredgames.connect.actions._
import com.consideredgames.connect.state.Connectivity
import com.consideredgames.connect.{ConnectionProtocols, _}
import com.consideredgames.message.Messages._
import com.consideredgames.user.actions.User
import diode.ActionResult.{ModelUpdate, ModelUpdateEffect}
import diode.{ActionBatch, RootModelRW}
import diode.data.Pot
import org.scalatest.{AsyncFunSuite, Matchers}

import scala.concurrent.Future


class ConnectionHandlerTest extends AsyncFunSuite with Matchers {

  import scala.concurrent.ExecutionContext.Implicits.global

  val messageSender = new MessageSender {
    override def send(m: Message): Unit = ???
  }

  trait handlerSetup {

    val connProtocols: ConnectionProtocols

    val initialState: Pot[Connectivity]

    val handler = new ConnectionHandler(connProtocols)(new RootModelRW(initialState))

    val register = Register("username", "hash", "email")
    val connectAction = ConnectAction(register)
  }

  trait successfulConnect {
    val connProtocols = new ConnectionProtocols {
      override def connect(request: ConnectRequest): Future[MessageSender] =
        Future successful messageSender
    }
  }

  trait failingConnect {

    val error = ServiceUnavailableException("server down")

    val connProtocols = new ConnectionProtocols {
      override def connect(request: ConnectRequest): Future[MessageSender] =
        Future failed error
    }
  }

  test("successful registration should have effect connected and pending state") {

    new successfulConnect with handlerSetup {

      val initialState = Pot.empty[Connectivity]

      val nextAction = handler.handleAction(initialState, connectAction) match {
        case Some(ModelUpdateEffect(newModel, effects)) =>
          assert(newModel.isPending)
          assert(effects.size == 1)
          // run effect
          effects.toFuture

        case r => fail(s"unexpected action result $r")
      }

      val assertion = nextAction.map {
        case batch: ActionBatch =>
          assert(batch.actions.contains(ConnectSuccess(messageSender)))
          assert(batch.actions.contains(User(register.username)))
        case x => fail(s"unexpected next action $x")
      }
    }.assertion

  }

  test("UNSUCCESSFUL registration should have effect ConnectFailed and pending state") {

    new failingConnect with handlerSetup {

      val initialState = Pot.empty[Connectivity]

      val nextAction = handler.handleAction(initialState, connectAction) match {
        case Some(ModelUpdateEffect(newModel, effects)) =>
          assert(newModel.isPending)
          assert(effects.size == 1)
          // run effect
          effects.toFuture

        case r => fail(s"unexpected action result $r")
      }

      val assertion = nextAction.map {
        case ConnectFailed(ex) => assert(ex == error)
        case x => fail(s"unexpected next action $x")
      }
    }.assertion

  }

  test("reconnect after disconnect should result in pending state") {

    new successfulConnect with handlerSetup {

      val initialState = Pot.empty[Connectivity].ready(Connectivity(connectedAt = DateUtils.nowAsMillis)).unavailable()

      val nextAction = handler.handleAction(initialState, connectAction) match {
        case Some(ModelUpdateEffect(newModel, effects)) =>
          assert(newModel.isPending)
          assert(effects.size == 1)
          // run effect
          effects.toFuture

        case r => fail(s"unexpected action result $r")
      }

      val assertion = nextAction.map {
        case batch: ActionBatch => assert(batch.actions.contains(ConnectSuccess(messageSender)))
        case x => fail(s"unexpected next action $x")
      }
    }.assertion

  }

  test("disconnect should result in unavailable state") {

    new successfulConnect with handlerSetup {

      val initialState = Pot.empty[Connectivity]

      val action = Disconnected

      val assertion = handler.handleAction(initialState, action) match {
        case Some(ModelUpdate(newModel)) =>
          assert(newModel.isUnavailable)

        case r => fail(s"unexpected action result $r")
      }
    }.assertion

  }

}
