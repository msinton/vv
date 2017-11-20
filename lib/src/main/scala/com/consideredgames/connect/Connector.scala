package com.consideredgames.connect

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.consideredgames.message.MessageMapper
import com.consideredgames.message.Messages._
import com.typesafe.config.Config
import diode.Dispatcher

import scala.concurrent.Future
import scala.util.Try

/**
  * Created by matt on 16/11/17.
  */
trait Connector {

  def open(r: Register): Future[MessageSender]

  def open(r: Login): Future[MessageSender]
}

class ConnectorImpl(name: String, config: Config, dispatch: Dispatcher)
                   (implicit val system: ActorSystem, implicit val materializer: ActorMaterializer) extends Connector {

  import akka.actor.TypedActor.dispatcher

  private val socket = new SocketMethods(dispatch, new WebsocketFlow())

  private val toSocketMessageSender: PartialFunction[Message, Try[MessageSender]] = {
    case LoginResponseSuccess(username, sessionId) => Try(socket.open(username, sessionId))
    case RegisterResponseSuccess(username, sessionId) => Try(socket.open(username, sessionId))
    case err: ConnectResponseError => Try(throw BadResponseException(err))
  }

  private def open(json: String, path: String): Future[MessageSender] =
    for {
      response <- HttpMethods.post(config, json, path)
      parsed <- httpResponseParse(response)
      sender <- Future.fromTry(toSocketMessageSender(parsed))
    } yield sender

  override def open(r: Register): Future[MessageSender] =
    open(MessageMapper.toJson(r), "register")

  override def open(r: Login): Future[MessageSender] =
    open(MessageMapper.toJson(r), "login")

}
