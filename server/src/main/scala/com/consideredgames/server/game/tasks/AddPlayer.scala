package com.consideredgames.server.game.tasks

import akka.actor.ActorRef
import com.consideredgames.game.model.player.PlayerColours.PlayerColour

case class AddPlayer(username: String, playerWorker: ActorRef, colour: PlayerColour)
