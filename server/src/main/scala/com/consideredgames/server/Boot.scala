package com.consideredgames.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.consideredgames.server.db.Indexes
import com.consideredgames.server.util.Binding
import com.typesafe.scalalogging.LazyLogging

import scala.util.Try

object Boot extends App with LazyLogging {

  implicit val system: ActorSystem             = ActorSystem("vv-server")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  import system.dispatcher

  val config = system.settings.config
  val host   = config.getString("app.host")
  val port   = config.getInt("app.port")

  lazy val shutdownHook = {
    logger.info("------------- Shutting down -------------")
    system.terminate()
    sys.exit(1)
  }

  Try {
    for {
      _ <- Indexes.initialise().failed.map(Indexes.handleFailure(shutdownHook))
      gameService = new GameService()
      bindingF    = Http().bindAndHandle(gameService.route, host, port)
      _           = bindingF.failed.map(Binding.handleFailure(shutdownHook))
      binding <- bindingF
      _ = Binding.logStatus(binding)
    } yield logger.info("----------------- Service started -----------------")
  }

}
