package com.consideredgames.activity.actions.handlers

import com.consideredgames.activity.StartGameException
import com.consideredgames.activity.actions.StartGame
import com.consideredgames.activity.state.Games
import com.consideredgames.game.model.animals.AnimalInfo
import com.consideredgames.game.model.game.{GameBuilder, GameData, NewGameConfig}
import com.consideredgames.game.model.person.tools.{ToolInfo, Tools}
import com.consideredgames.message.Messages.{JoinResponseSuccess, NewGameReady, NewGameResponse}
import com.consideredgames.user.{Profile, Username}
import diode.data.Pot
import diode.{ActionHandler, ActionResult, ModelRO, ModelRW}

import scala.util.{Failure, Success, Try}

class GameActivityHandler[M](modelRW: ModelRW[M, Games], profile: ModelRO[Option[Profile]])
    extends ActionHandler(modelRW) {

  private def readyGameToGameConfig(readyGame: NewGameReady): Try[NewGameConfig] = {
    val opts           = readyGame.newGameOptions
    val animalInfosTry = opts.animalInfos.fold(AnimalInfo.importFromFile())(AnimalInfo.readFromString)
    val toolsTry: Try[Tools] = opts.tools.fold(Try(Tools())) { json =>
      ToolInfo.readFromString(json) map { toolInfo =>
        Tools(toolInfo)
      }
    }
    for {
      animalInfos <- animalInfosTry
      tools       <- toolsTry
      config = NewGameConfig(readyGame.players, readyGame.seed, animalInfos, tools, List())
    } yield config
  }

  val startGameException = StartGameException("Game failed to start")

  // TODO value.ready and Pot.ready confusing, consider if splitting this into other handler will help
  private def startGame(gameId: String): Pot[GameData] = {
    val game: Try[GameData] = for {
      readyGame <- value.ready
        .find(_.gameId == gameId)
        .fold[Try[NewGameReady]](Failure(startGameException))(Success(_))
      config   <- readyGameToGameConfig(readyGame)
      username <- profile.value.fold[Try[Username]](Failure(startGameException))(p => Success(p.username))
    } yield GameBuilder.build(config, username)

    game match {
      case Success(gd) => value.active.ready(gd)
      case Failure(ex) => value.active.fail(ex)
    }
  }

  override protected def handle: PartialFunction[Any, ActionResult[M]] = {

    case JoinResponseSuccess(gameId) => updated(value.copy(joined = value.joined + gameId))

    case NewGameResponse(gameId) => updated(value.copy(joined = value.joined + gameId))

    case m: NewGameReady => updated(value.copy(ready = value.ready + m))

    case StartGame(gameId) => updated(value.copy(active = startGame(gameId)))

  }
}
