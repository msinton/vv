package com.consideredgames.connect

import akka.actor.{ActorSystem, TypedActor, TypedProps}
import akka.stream.ActorMaterializer
import com.consideredgames.message.Messages.{ConnectRequest, Login, Register}
import com.typesafe.config.Config
import diode.Dispatcher

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by matt on 19/11/17.
  */
trait ConnectionProtocols {
  def connect(request: ConnectRequest): Future[MessageSender]
}

class ConnectionProtocolsImpl(dispatcher: Dispatcher, config: Config)
                             (implicit val system: ActorSystem, implicit val materializer: ActorMaterializer)
  extends ConnectionProtocols {

  implicit val timeout: akka.util.Timeout = 10.seconds

  private val connector: Connector = TypedActor(TypedActor.context).typedActorOf(
    TypedProps(classOf[Connector], new ConnectorImpl("server-connector", config, dispatcher)))

  def connect(request: ConnectRequest): Future[MessageSender] = request match {
    case r: Register => connector.open(r)
    case r: Login => connector.open(r)
  }
}