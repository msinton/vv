package com.consideredgames.server

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.RemoteAddress
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.ActorMaterializer
import com.consideredgames.message.Messages.{Login, Register, Message => VVMessage}
import com.consideredgames.server.game.LobbyActor
import com.consideredgames.server.routes.WebsocketFlow
import com.consideredgames.server.routes.sessions.{LoginActor, RegisterSupervisor, SessionActor}
import com.consideredgames.server.routes.tasks.MessageWithIp
import com.consideredgames.server.serializers.JsonSupport

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class GameService(implicit val actorSystem: ActorSystem,
                  implicit val actorMaterializer: ActorMaterializer,
                  implicit val executionContext: ExecutionContext)
    extends Directives
    with JsonSupport {

  import akka.pattern.ask

  implicit val timeout: akka.util.Timeout = 10.seconds

  private val lobbyWorker        = actorSystem.actorOf(Props(LobbyActor()), "lobby-worker")
  private val sessionWorker      = actorSystem.actorOf(Props(SessionActor(lobbyWorker)), "session-worker")
  private val registerSupervisor = actorSystem.actorOf(Props(RegisterSupervisor()), "register-supervisor")
  private val loginWorker        = actorSystem.actorOf(Props(LoginActor()), "login-worker")

  private val websocketFlow = WebsocketFlow(sessionWorker)

  private def defaultIpToUnknown(ip: RemoteAddress) = ip.toOption.map(_.getHostAddress).getOrElse("unknown")

  def registerRoute(register: Register, ip: String): Route = {
    val workerResponse = registerSupervisor ? MessageWithIp(register, ip)
    onSuccess(workerResponse) {
      case m: VVMessage => complete(m)
    }
  }

  def loginRoute(login: Login, ip: String): Route = {
    val workerResponse = loginWorker ? MessageWithIp(login, ip)
    onSuccess(workerResponse) {
      case m: VVMessage => complete(m)
    }
  }

  val route: Route =
    get {
      (pathSingleSlash & parameter("username") & parameter("sessionId") & extractClientIP) {
        (username, sessionId, ip) =>
          handleWebSocketMessages(websocketFlow.flow(username, sessionId, defaultIpToUnknown(ip)))
      }
    } ~ post {
      (path("register") & entity(as[Register]) & extractClientIP) { (register, ip) =>
        registerRoute(register, defaultIpToUnknown(ip))
      } ~
        (path("login") & entity(as[Login]) & extractClientIP) { (login, ip) =>
          loginRoute(login, defaultIpToUnknown(ip))
        }
    }
}
