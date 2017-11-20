package com.consideredgames.connect.action.handlers

import com.consideredgames.api.rootmodel.Messages
import com.consideredgames.common.DateUtils
import com.consideredgames.connect.{ConnectSuccess, Disconnected, MessageSender}
import com.consideredgames.message.Messages.Request
import diode.{ActionHandler, ModelRW}

/**
  * Created by matt on 19/11/17.
  */
class SocketMessageHandler[M](modelRW: ModelRW[M, Messages]) extends ActionHandler(modelRW) {

  var messageSender: Option[MessageSender] = None

  def handle = {

    case ConnectSuccess(sender) =>
      messageSender = Option(sender)
      noChange

    case Disconnected =>
      messageSender = None
      noChange

    case m: Request => messageSender match {
      case Some(sender) =>
        sender.send(m)
        updated(value.copy(history = m :: value.history))

      case None =>
        updated(value.copy(unsent = (DateUtils.nowAsMillis, m) :: value.unsent))
    }

  }
}