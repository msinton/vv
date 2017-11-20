package com.consideredgames.common

import java.time.{LocalDateTime, ZoneId}

/**
  * Created by matt on 19/11/17.
  */
object DateUtils {

  def nowAsMillis: Millis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli

}
