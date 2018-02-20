package com.consideredgames.game.model.game

import com.consideredgames.game.model.board.BoardUtils
import com.consideredgames.game.model.person.tools.ToolUtils
import com.consideredgames.game.model.player.PlayerWithPeople

case class GameData(boardUtils: BoardUtils,
                    playerData: Map[String, PlayerWithPeople],
                    toolUtils: ToolUtils,
                    gameProcessors: GameProcessors,
                    gameState: GameState,
                    controllers: GameControllers)