package com.consideredgames.server.routes.tasks

import com.consideredgames.message.Messages.Message

case class MessageWithIp(m: Message, ip: String)
