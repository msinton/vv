package com.consideredgames.api

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.consideredgames.activity.actions.handlers.GameActivityHandler
import com.consideredgames.api.rootmodel.RootModel
import com.consideredgames.connect._
import com.consideredgames.connect.actions.handlers.{ConnectionHandler, SocketMessageHandler}
import com.consideredgames.user.actions.handlers.ProfileHandler
import diode.Circuit

import scala.concurrent.ExecutionContext

object AppCircuit {

  def apply(): AppCircuit = {

    import ExecutionContext.Implicits.global

    new AppCircuit()
  }
}

class AppCircuit(implicit val ec: ExecutionContext) extends Circuit[RootModel] {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  lazy val connectionProtocols = new ConnectionProtocolsImpl(this, system.settings.config)

  override protected def initialModel: RootModel = RootModel()

  override protected def actionHandler: HandlerFunction = composeHandlers(
    foldHandlers(
      new ConnectionHandler(connectionProtocols)(zoomTo(_.connectivity)),
      new SocketMessageHandler(zoomTo(_.messages))
    ),
    new ProfileHandler(zoomTo(_.profile)),
    new GameActivityHandler(zoomTo(_.activity.games), zoomTo(_.profile))
  )
}



