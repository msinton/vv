package com.consideredgames.game.model.hex

case class RiverSegment(hexA: Hex, sideA: Side) extends BordersHex {

  var flow: Option[Flow] = None

  /**
   * Gets the neighbouring river segments at either the rivers 'start' point or its 'end' point.
   * There may be 2 since it is possible for 3 rivers to meet at a point.
   *
   * @param inflowDirection If true then returns the segment(s) which are joined to the <code>from</code> point, or <i>in flow</i> direction of this river.
   *                        Otherwise the segments at the <i>out flow</i> direction are returned, along with their respective <code>from</code> points.
   * @return Map of riverSegment to point
   */
  def getNeighbour(inflowDirection: Boolean): Iterable[(RiverSegment, Point)] = {

    def findNeighbourAtPoint(hex: Hex, side: Side, point: Point): Option[(RiverSegment, Point)] = {

      val isPointOnClockwiseVertexOfSide = hex.getVertex(point).contains(side.clockwiseVertex)
      // we want to look for rivers that are on the next side of the hex, continuing in the same direction
      if (isPointOnClockwiseVertexOfSide)
        hex.rivers.get(side.clockwise) map {(_, point)}
      else
        hex.rivers.get(side.anticlockwise) map {(_, point)}
    }

    val hexANeighbour = for {
      flw <- flow
      point = if (inflowDirection) flw.to else flw.from
      n <- findNeighbourAtPoint(hexA, sideA, point)
    } yield n

    val hexBNeighbour = for {
      flw <- flow
      point = if (inflowDirection) flw.to else flw.from
      hex <- hexB
      side <- sideB
      n <- findNeighbourAtPoint(hex, side, point)
    } yield n

    hexANeighbour ++ hexBNeighbour
  }

  def neighbours(): Map[RiverSegment, Point] =
    (getNeighbour(inflowDirection = true) ++ getNeighbour(inflowDirection = false)).toMap

  /**
   * Sets the Flow as flowing from the fromPoint to the toPoint
   */
  def setFlowUsingFrom(fromPoint: Point) {
    val toPoint = otherPoint(fromPoint)
    toPoint foreach {
      setFlow(fromPoint, _)
    }
  }

  def setFlow(fromPoint: Point, toPoint: Point) = {
    flow = Option(Flow(fromPoint, toPoint))
  }

  /**
   * @return True if this flows towards the hex i.e. the "to" point lies on the hex.
   */
  def flowsTowards(hex: Hex): Boolean = {
    flow.fold(false)(flw => hex.getVertex(flw.to).isDefined)
  }

  /**
   * @return True if this flows towards the otherRiver
   */
  def flowsTowards(otherRiver: RiverSegment): Boolean = {
    flow.fold(false)(flw => otherRiver.otherPoint(flw.to).isDefined)
  }

  override def equals(o: Any) = {
    o match {
      case that : RiverSegment => (hexA == that.hexA && sideA == that.sideA) || (hexB.contains(that.hexA) && sideB.contains(that.sideA))
      case _ => false
    }
  }
}

object RiverSegment {

  /**
   * Effectively a safe constructor - will not create river if there already is one at the location.
   */
  def create(rivers: Iterable[RiverSegment], hexA: Hex, sideA: Side): Option[RiverSegment] = {

    Option(BordersHex.listContainsBordersHexAtLocation(rivers.toArray, hexA, sideA)).collect {
      case false => RiverSegment(hexA, sideA)
    }
  }
}

case class Flow(from: Point, to: Point)