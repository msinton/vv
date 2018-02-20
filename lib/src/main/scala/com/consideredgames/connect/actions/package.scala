package com.consideredgames.connect

import com.consideredgames.message.Messages.ConnectRequest
import diode.Action

/**
  * Created by matt on 25/11/17.
  */
package object actions {

  case class ConnectSuccess(sender: MessageSender) extends Action

  case class ConnectAction(request: ConnectRequest) extends Action

  case class ConnectFailed(ex: Throwable) extends Action

  case object Disconnected extends Action
}
