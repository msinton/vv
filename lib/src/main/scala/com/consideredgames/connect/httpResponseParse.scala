package com.consideredgames.connect

import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.consideredgames.message.Messages.Message
import com.consideredgames.serializers.JsonSupport

import scala.concurrent.Future

object httpResponseParse extends JsonSupport {

  def apply(response: HttpResponse)(implicit materializer: ActorMaterializer): Future[Message] = {
    response match {
      case HttpResponse(_: StatusCodes.Success, headers, entity: HttpEntity.Strict, _) =>
        println("http res parser", entity, headers)
        Unmarshal(entity).to[Message]

      case HttpResponse(StatusCodes.ServiceUnavailable, headers, entity, _) =>
        println("server down - try again later", entity, headers)
        Future failed ServiceUnavailableException("server down")

      case HttpResponse(status, headers, entity, _) =>
        println("bad status", status, entity, headers)
        Future failed BadStatusException(status)
    }
  }
}
