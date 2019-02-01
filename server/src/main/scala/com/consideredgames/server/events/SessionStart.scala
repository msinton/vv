package com.consideredgames.server.events

import akka.actor.ActorRef

case class SessionStart(username: String, sessionId: String, ip: String, actorRef: ActorRef) extends Event
