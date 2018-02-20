package com.consideredgames.connect.state

import com.consideredgames.common.Millis
import com.consideredgames.message.Messages.Message


case class Messages(history: List[Message] = Nil, unsent: List[(Millis, Message)] = Nil)
