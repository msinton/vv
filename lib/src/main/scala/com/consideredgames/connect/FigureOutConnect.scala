package com.consideredgames.connect

object FigureOutConnect {

  // Conn Proto
  // creates typed actor Connector
  // delegates to it for register and login

  // Connector
  // creates SocketMethods
  // directs Login and Register to correct http path
  // serializing requests to json
  // returns Future[MessageSender]
  // formats failed responses
  // - assuming no connection

  /*


  private val toSocketMessageSender: PartialFunction[Message, Try[MessageSender]] = {
    case LoginResponseSuccess(username, sessionId)    => Try(socket.open(username, sessionId))
    case RegisterResponseSuccess(username, sessionId) => Try(socket.open(username, sessionId))
    case err: ConnectResponseError                    => Try(throw BadResponseException(err))
  }

  private def openJson(json: String, path: String): Future[MessageSender] =
    for {
      response <- formatFailedHttp(HttpMethods.post(config, json, path))
      parsed   <- httpResponseParse(response)
      sender   <- Future.fromTry(toSocketMessageSender(parsed))
    } yield sender


    host, port, (username, sessionId)
    ->
    webSocketFlow
    ->
    source ... run (materializer)

    httpResponseParse - needs ActorMaterializer and Unmarshals the http response
    // rename and move into HttpHandler / HttpUtils, parseHttpResponse
    // make more generic by supplying Message as Type param

    HttpMethods - belongs alongside parseHttpResponse
    // json requests
    // needs host, port. Turn into a factory that returns the methods for a given address

    ConnectorImpl
    // remove config

    ConnectionHandler
    // wants to connect - ConnectAction
    // handles state





 */

}
