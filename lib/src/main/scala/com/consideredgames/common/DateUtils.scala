package com.consideredgames.common

import java.time.{LocalDateTime, ZoneId}

object DateUtils {

  def nowAsMillis: Millis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli

}
