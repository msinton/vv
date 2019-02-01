package com.consideredgames.activity.state

import com.consideredgames.game.model.game.GameData
import com.consideredgames.message.Messages.NewGameReady
import diode.data.Pot

case class Activity(games: Games = Games())

case class Games(ready: Set[NewGameReady] = Set(), joined: Set[String] = Set(), active: Pot[GameData] = Pot.empty)
