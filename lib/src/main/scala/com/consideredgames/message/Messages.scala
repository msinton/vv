package com.consideredgames.message

import com.consideredgames.game.model.player.PlaceholderPlayer
import com.consideredgames.game.model.player.PlayerColours.PlayerColour
import com.consideredgames.user.Username
import diode.Action

object Messages {

  sealed trait Message extends Action

  trait Request extends Message

  trait ConnectResponseError extends Message
  trait ConnectRequest extends Request {
    def username: Username
  }

  case class SessionStarted() extends Message

  case class Register(username: Username, passwordHash: String, email: String) extends ConnectRequest

  case class RegisterResponseSuccess(username: Username, sessionId: String) extends Message
  case class RegisterResponseUsernameUnavailable(suggestions: List[String]) extends ConnectResponseError
  case class RegisterResponseInvalid(reasons: List[String]) extends ConnectResponseError

  case class Login(username: Username, passwordHash: String, email: String) extends ConnectRequest

  case class LoginResponseSuccess(username: String, sessionId: String) extends Message
  case class LoginResponseInvalid(reasons: List[String]) extends ConnectResponseError

  case class Logout() extends Request
  case class ForceLogout(username: Username) extends Message

  case class Join(myColour: PlayerColour, gameId: Option[String] = None) extends Request
  case class JoinResponseSuccess(gameId: String) extends Message
  case class JoinResponseFailure(reasons: List[String]) extends Message

  case class Quit(gameId: String) extends Request

  case class NewGameOptions(privateGame: Boolean = false,
                            animalInfos: Option[String] = None,
                            tools: Option[String] = None,
                            interchangeableTools: Option[String] = None,
                            startingTools: collection.Map[String, Int] = collection.Map.empty)

  // Instead of joining a game
  case class NewGameRequest(numberOfPlayers: Int = 3,
                            myColour: PlayerColour,
                            seed: Option[Long] = None,
                            newGameOptions: Option[NewGameOptions] = None) extends Request

  case class NewGameResponse(gameId: String) extends Message
  case class NewGameResponseFailure(reasons: List[String]) extends Message

  case class NewGameReady(gameId: String,
                          players: List[PlaceholderPlayer],
                          seed: Long,
                          newGameOptions: NewGameOptions) extends Message


  // To start the game before all player spaces filled
  case class NewGameStartRequest(gameId: String) extends Request

  val classes = List(classOf[Register], classOf[RegisterResponseSuccess], classOf[RegisterResponseUsernameUnavailable],
    classOf[RegisterResponseInvalid], classOf[Login], classOf[LoginResponseSuccess], classOf[LoginResponseInvalid],
    classOf[Logout], classOf[Join], classOf[JoinResponseSuccess], classOf[JoinResponseFailure], classOf[Quit],
    classOf[NewGameResponse], classOf[NewGameRequest], classOf[NewGameStartRequest], classOf[SessionStarted],
    classOf[ForceLogout], classOf[PlaceholderPlayer], classOf[NewGameOptions], classOf[NewGameReady])

}

