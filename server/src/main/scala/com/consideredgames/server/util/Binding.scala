package com.consideredgames.server.util
import akka.http.scaladsl.Http.ServerBinding
import com.typesafe.scalalogging.LazyLogging

object Binding extends LazyLogging {

  def logStatus(serverBinding: ServerBinding): Unit = {
    val localAddress = serverBinding.localAddress
    logger.info(s"Server is listening on ${localAddress.getHostName}:${localAddress.getPort}")
  }

  def handleFailure(action: => Any)(e: Throwable): Unit = {
    logger.error("Binding failed", e)
    action
  }

}
