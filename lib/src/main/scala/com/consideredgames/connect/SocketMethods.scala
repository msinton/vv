package com.consideredgames.connect

import akka.Done
import akka.stream.scaladsl.Sink
import com.consideredgames.message.Messages.Message
import diode.Dispatcher

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * Created by matt on 03/05/17.
  */
class SocketMethods(dispatch: Dispatcher,
                    connector: WebsocketFlow
                  )(implicit ex: ExecutionContext) {

  def open(username: String, sessionId: String): MessageSender = {

    val ((requestActor, upgradeResponse), streamEnd) = connector.run(sink, username, sessionId)

    upgradeResponse.onComplete {
      case Success(_) => println("Upgrade to websocket successful")
//        dispatch(Connected) // TODO do I need this?

      case Failure(e) => println("Upgrade to websocket failed", e)
        dispatch(Disconnected)
    }

    streamEnd.onComplete {
      case Success(_) => println("Stream completed successfully")
        dispatch(Disconnected)
      case Failure(e) => println("Stream completed bad - probably internet/server down", e)
        dispatch(Disconnected)
    }

    new MessageSender {
      def send(m: Message): Unit = requestActor ! m
    }
  }

  def sink: Sink[Message, Future[Done]] = Sink.foreach[Message](dispatch.apply)

}


