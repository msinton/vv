package com.consideredgames.server.game.tasks

import akka.actor.ActorRef
import com.consideredgames.message.Messages.Request

case class RequestWithPlayer(request: Request, username: String, playerWorker: ActorRef)
