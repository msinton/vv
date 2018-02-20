package com.consideredgames

import akka.http.scaladsl.model.StatusCode
import com.consideredgames.message.Messages.Message


package object connect {

  trait MessageSender {
    def send(m: Message): Unit
  }

  case class ServiceUnavailableException(s: String = null, exception: Throwable = null)
    extends RuntimeException(s, exception)

  case class BadStatusException(statusCode: StatusCode, exception: Throwable = null)
    extends RuntimeException(s"bad status: $statusCode", exception)

  case class BadResponseException(m: Message, exception: Throwable = null)
    extends RuntimeException(s"$m", exception)

}
