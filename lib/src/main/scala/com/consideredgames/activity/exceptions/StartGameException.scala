package com.consideredgames.activity.exceptions

case class StartGameException(reason: String, exception: Throwable = null) extends RuntimeException(reason, exception)
