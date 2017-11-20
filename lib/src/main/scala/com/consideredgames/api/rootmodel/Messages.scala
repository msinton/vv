package com.consideredgames.api.rootmodel

import com.consideredgames.common.Millis
import com.consideredgames.message.Messages.Message

/**
  * Created by matt on 19/11/17.
  */
case class Messages(history: List[Message] = Nil, unsent: List[(Millis, Message)] = Nil)
