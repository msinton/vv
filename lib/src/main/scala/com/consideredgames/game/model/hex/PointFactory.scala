package com.consideredgames.game.model.hex

class PointFactory {

  private var count = 0

  def create: Point = {
    val p = Point(count)
    count += 1
    p
  }
}