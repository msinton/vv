package com.consideredgames.user

import diode.Action

package object actions {

  case class User(username: Username) extends Action

}
