package com.consideredgames.connect

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.StatusCodes.Success
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse, HttpMethods => verbs}
import akka.stream.ActorMaterializer
import com.typesafe.config.Config

import scala.concurrent.Future

/**
  * Created by matt on 16/11/17.
  */
object HttpMethods {

  def post(config: Config, json: String, path: String)
          (implicit system: ActorSystem, materializer: ActorMaterializer): Future[HttpResponse] = {
    val host = config.getString("app.host")
    val port = config.getInt("app.port")
    val requestEntity = HttpEntity(`application/json`, json)

    Http().singleRequest(
      HttpRequest(
        verbs.POST,
        uri = s"http://$host:$port/$path",
        entity = requestEntity
      )
    )
  }
}
