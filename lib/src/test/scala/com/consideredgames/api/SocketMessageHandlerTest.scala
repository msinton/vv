package com.consideredgames.api

import com.consideredgames.api.rootmodel.Messages
import com.consideredgames.common.DateUtils
import com.consideredgames.connect.action.handlers.SocketMessageHandler
import com.consideredgames.connect.{ConnectSuccess, Disconnected, MessageSender}
import com.consideredgames.game.model.player.PlayerColours
import com.consideredgames.message.{Messages => VVMessages}
import com.consideredgames.message.Messages.{Logout, Message, NewGameRequest}
import diode.ActionResult.{ModelUpdate, NoChange}
import diode.RootModelRW
import org.scalatest.FunSuite

/**
  * Created by matt on 19/11/17.
  */
class SocketMessageHandlerTest extends FunSuite {

  trait handlerSetup {

    val initialState: Messages

    val handler = new SocketMessageHandler(new RootModelRW(initialState))

    val actionNewGame = NewGameRequest(myColour = PlayerColours.DarkGreen)

    var sentBySender = List.empty[Message]

    val messageSender = new MessageSender {
      override def send(m: VVMessages.Message): Unit = sentBySender = m :: sentBySender
    }

    val connectSuccess = ConnectSuccess(messageSender)

    val disconnected = Disconnected
  }

  trait stateConnected {
    val handler: SocketMessageHandler[Messages]
    val messageSender: MessageSender
    handler.messageSender = Some(messageSender)
  }

  test("connect success should set message sender") {
    new handlerSetup {
      override val initialState: Messages = Messages()

      handler.handleAction(initialState, connectSuccess) match {
        case Some(NoChange) => assert(true)

        case x => fail(s"unexpected action result $x")
      }

      assert(handler.messageSender.nonEmpty)
    }
  }

  test("Disconnected should set message sender to None") {
    new handlerSetup with stateConnected {
      override val initialState: Messages = Messages()

      handler.handleAction(initialState, disconnected) match {
        case Some(NoChange) => assert(true)

        case x => fail(s"unexpected action result $x")
      }

      println(handler.messageSender)
      assert(handler.messageSender.isEmpty)
    }
  }

  test("In state connected, new message should be handled by sender") {
    new handlerSetup with stateConnected {
      override val initialState: Messages = Messages()

      handler.handleAction(initialState, actionNewGame)

      assert(sentBySender.size === 1)
      assert(sentBySender.head == actionNewGame)
    }
  }

  test("In state connected, new message should be added to history") {
    new handlerSetup with stateConnected {
      override val initialState: Messages = Messages()

      handler.handleAction(initialState, actionNewGame) match {
        case Some(ModelUpdate(newModel)) =>
          assert(newModel.history.nonEmpty)
          assert(newModel.history.head == actionNewGame)
          assert(newModel.unsent.isEmpty)

        case x => fail(s"unexpected action result $x")
      }
    }
  }

  test("In state disconnected, new message should Not be handled") {
    new handlerSetup {
      override val initialState: Messages = Messages()

      handler.handleAction(initialState, actionNewGame)

      assert(sentBySender.isEmpty)
    }
  }

  test("In state disconnected, new message should be added to unsent") {
    val startTime = DateUtils.nowAsMillis
    new handlerSetup {
      override val initialState: Messages = Messages(unsent = List((startTime, Logout())))

      handler.handleAction(initialState, actionNewGame) match {
        case Some(ModelUpdate(newModel)) =>
          assert(newModel.unsent.size == 2)
          assert(newModel.unsent.head._2 == actionNewGame)
          assert(newModel.unsent.head._1 >= startTime)
          assert(newModel.history.isEmpty)

        case x => fail(s"unexpected action result $x")
      }
    }
  }

}
