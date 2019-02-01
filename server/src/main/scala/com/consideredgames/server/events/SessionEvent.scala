package com.consideredgames.server.events
import com.consideredgames.message.Messages.{Message => VVMessage}

case class SessionEvent(vvMessage: VVMessage, sessionId: String) extends Event
