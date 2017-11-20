package com.consideredgames

import akka.http.scaladsl.model.StatusCode
import com.consideredgames.message.Messages.{ConnectRequest, Message}
import diode.Action

/**
  * Created by matt on 15/11/17.
  */
package object connect {

  trait MessageSender { def send(m: Message): Unit}

  case class ConnectSuccess(sender: MessageSender) extends Action

  case class ConnectAction(request: ConnectRequest) extends Action

  case class ConnectFailed(ex: Throwable) extends Action

  case object Disconnected extends Action

  case class ServiceUnavailableException(s: String = null, exception: Throwable = null)
    extends RuntimeException(s, exception)

  case class BadStatusException(statusCode: StatusCode, exception: Throwable = null)
    extends RuntimeException(s"bad status: $statusCode", exception)

  case class BadResponseException(m: Message, exception: Throwable = null)
    extends RuntimeException(s"$m", exception)
}
