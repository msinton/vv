package com.consideredgames.api

import akka.actor.{ActorSystem, TypedActor, TypedProps}
import akka.stream.ActorMaterializer
import com.consideredgames.api.rootmodel.RootModel
import com.consideredgames.connect._
import com.consideredgames.connect.action.handlers.{ConnectionHandler, SocketMessageHandler}
import com.consideredgames.message.Messages.{ConnectRequest, Login, Register}
import com.typesafe.config.Config
import diode.{Circuit, Dispatcher}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by matt on 14/11/17.
  */
object AppCircuit {

  def apply(): AppCircuit = {

    import ExecutionContext.Implicits.global

    lazy val app = new AppCircuit()

    app
  }
}

class AppCircuit(implicit val ec: ExecutionContext) extends Circuit[RootModel] {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  lazy val connectionProtocols = new ConnectionProtocolsImpl(this, system.settings.config)

  override protected def initialModel: RootModel = RootModel()

  override protected def actionHandler: HandlerFunction = foldHandlers(
    new ConnectionHandler(connectionProtocols)(zoomTo(_.connectivity)),
    new SocketMessageHandler(zoomTo(_.messages))
  )
}



