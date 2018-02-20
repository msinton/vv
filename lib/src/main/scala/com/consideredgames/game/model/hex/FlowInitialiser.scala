package com.consideredgames.game.model.hex

import com.consideredgames.common.Utils
import com.typesafe.scalalogging.LazyLogging

import scala.annotation.tailrec
import scala.util.Random

class FlowInitialiser(random: Random) extends LazyLogging {

  def setup(rivers: Iterable[RiverSegment]): Unit = {
    setupFlowsFromSources(rivers)
    setupFlowsFromRandom(rivers)
  }

  // A source is a point where there are 3 rivers connected
  def setupFlowsFromSources(rivers: Iterable[RiverSegment]): Unit = {

    val riversWithFlow = findSources(rivers)
      .flatMap(point => point.getRivers.map(setFlowFrom(_, point)))

    logger.debug(s"Got sources: $riversWithFlow")
    continueRiverFlow(riversWithFlow.toList)
  }

  @tailrec
  private final def setupFlowsFromRandom(rivers: Iterable[RiverSegment]): Unit = {

    rivers.filter(r => r.flow.isEmpty) match {
      case Nil =>

      case riversWithoutFlow =>

        val randomRiver = Utils.getRandom(riversWithoutFlow, random)
        logger.debug(s"Setting random flow $randomRiver")

        val next =
          setRandomFlow(randomRiver).getNeighbours(inflowDirection = true).keys.map(setFlowFrom(_, randomRiver.flow.get.to)) ++
            randomRiver.getNeighbours(inflowDirection = false).keys.map(setFlowTowards(_, randomRiver.flow.get.from))

        logger.debug(s"Random flow next: $next")
        continueRiverFlow(next.toList)

        setupFlowsFromRandom(riversWithoutFlow)
    }
  }

  private def setRandomFlow(river: RiverSegment): RiverSegment = {
    val vertex = if (random.nextBoolean()) river.sideA.clockwiseVertex else river.sideA.anticlockwiseVertex
    river.setFlowUsingFrom(river.hexA.vertices(vertex))
    river
  }

  private def setFlowFrom(riverSegment: RiverSegment, fromPoint: Point): RiverSegment = {
    riverSegment.setFlowUsingFrom(fromPoint)
    riverSegment
  }

  private def setFlowTowards(riverSegment: RiverSegment, fromPoint: Point): RiverSegment = {
    riverSegment.setFlowUsingFrom(fromPoint)
    riverSegment
  }

  @tailrec
  private final def continueRiverFlow(riversWithFlow: List[RiverSegment]): Unit = {

    riversWithFlow match {
      case Nil => logger.debug("<--- continue river flow finished")
      case _ => continueRiverFlow(riversWithFlow.flatMap(r => {
        val point = r.flow.get.to
        point.getRivers.filter(_.flow.isEmpty).map(setFlowFrom(_, point)) //TODO see if this works by doing getNeighbours??
      })) //TODO or delete getNeighbours - unnecessarily complex!
    }
  }

  def findSources(rivers: Iterable[RiverSegment]): Set[Point] = {
    rivers
      .flatMap(r => Seq(
        r.hexA.vertices(r.sideA.anticlockwiseVertex()),
        r.hexA.vertices(r.sideA.clockwiseVertex())))
      .groupBy(p => p)
      .filter(_._2.size == 3)
      .keySet
  }
}
