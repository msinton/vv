package com.consideredgames.connect

import akka.Done
import akka.stream.scaladsl.Sink
import com.consideredgames.connect.actions.Disconnected
import com.consideredgames.message.Messages.Message
import com.typesafe.scalalogging.LazyLogging
import diode.Dispatcher

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class SocketMethods(dispatch: Dispatcher, connector: WebsocketFlow)(implicit ex: ExecutionContext) extends LazyLogging {

  def open(username: String, sessionId: String): MessageSender = {

    val ((requestActor, upgradeResponse), streamEnd) = connector.run(sink, username, sessionId)

    upgradeResponse.onComplete {
      case Success(_) => logger.debug("Upgrade to websocket successful")
//        dispatch(Connected) // TODO do I need this?

      case Failure(e) =>
        logger.warn("Upgrade to websocket failed", e)
        dispatch(Disconnected)
    }

    streamEnd.onComplete {
      case Success(_) =>
        logger.debug("Stream completed successfully")
        dispatch(Disconnected)
      case Failure(e) =>
        logger.debug("Stream completed bad - probably internet/server down", e)
        dispatch(Disconnected)
    }

    m: Message =>
      requestActor ! m
  }

  def sink: Sink[Message, Future[Done]] = Sink.foreach[Message](dispatch.apply)

}
