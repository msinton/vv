package com.consideredgames.connect

import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.consideredgames.message.Messages.Message
import com.consideredgames.serializers.JsonSupport
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future

object httpResponseParse extends JsonSupport with LazyLogging {

  def apply(response: HttpResponse)(implicit materializer: ActorMaterializer): Future[Message] =
    response match {
      case HttpResponse(_: StatusCodes.Success, headers, entity: HttpEntity.Strict, _) =>
        logger.debug("http res parser entity {} headers {}", entity, headers)
        Unmarshal(entity).to[Message]

      case HttpResponse(StatusCodes.ServiceUnavailable, headers, entity, _) =>
        logger.debug("server down - try again later entity {} headers {}", entity, headers)
        Future failed ServiceUnavailableException("server down")

      case HttpResponse(status, headers, entity, _) =>
        logger.debug("bad status, status: {} entity: {} headers: {}", status, entity, headers)
        Future failed BadStatusException(status)
    }
}
