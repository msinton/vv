package com.consideredgames

package object activity {

  case class StartGameException(reason: String, exception: Throwable = null) extends RuntimeException(reason, exception)
}
