package com.consideredgames.server.game

import akka.actor.{Actor, ActorRef}
import com.consideredgames.server.game.tasks.{GameIsActive, StartGame}

class GamesActor extends Actor {

  private val games = collection.mutable.LinkedHashMap[String, ActorRef]()

  override def receive: Receive = {
    case r @ StartGame(gameId, gameWorker, isPrivate) =>
      games += (gameId -> gameWorker)
      sender() ! GameIsActive(gameId, isPrivate)
      gameWorker ! r

  }
}
