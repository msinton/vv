package com.consideredgames.connect

import com.consideredgames.message.Messages.ConnectRequest
import diode.Action

package object actions {

  case class ConnectSuccess(sender: MessageSender) extends Action

  case class ConnectAction(request: ConnectRequest) extends Action

  case class ConnectFailed(ex: Throwable) extends Action

  case object Disconnected extends Action
}
