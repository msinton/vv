package com.consideredgames.server.game.tasks

import akka.actor.ActorRef

case class StartGame(gameId: String, gameWorker: ActorRef, isPrivate: Boolean)
