package com.consideredgames.game.model.hex

import com.typesafe.scalalogging.LazyLogging

import scala.collection._
import scala.util.Random

/**
 * use the edges of a hex - shared by 2 hexes randomly generated river network
 *
 * splits board into even number of hexes - as many sections as players
 */

class RiverNetwork(random: Random) extends LazyLogging {

  // TODO get rid of this!?
  private var _rivers: mutable.Buffer[RiverSegment] = mutable.Buffer.empty[RiverSegment]
  private var _groups: Seq[HexGroup] = Nil

  private def neighboursNotInGroup(hex: Hex, group: Set[Hex]): Set[Hex] = {
    hex.neighbours.values.toSet.diff(group)
  }

  def applyRiversToBorders(hexGroups: Seq[HexGroup]): Seq[RiverSegment] = {
    val hexBoundaries = for {
      group <- hexGroups.tail
      hex <- group
      boundaryHex <- neighboursNotInGroup(hex, group)
      side <- hex.getSide(boundaryHex)
    } yield (hex, side)

    hexBoundaries.foldLeft(Seq.empty[RiverSegment])({
      case (rivers, (hex, side)) => RiverSegment.create(rivers, hex, side).fold(rivers)(_ +: rivers)
    })
  }

  def generate(hexes: Seq[Hex], numPlayers: Int): (Seq[RiverSegment], Seq[HexGroup]) = {
    val numGroups = if (numPlayers == 1) 2 else numPlayers
    _groups = new HexGridDivider(random).divideIntoRoughlyEqualRandomlyShaped(hexes.toIndexedSeq, numGroups)
    _rivers = applyRiversToBorders(_groups).toBuffer
    setupFlow(_rivers)
    (_rivers, _groups)
  }

  def rivers: Seq[RiverSegment] = _rivers

  def groups: Seq[HexGroup] = _groups

  /**
   * Enables the flow to be setup, does nothing if already setup.
   */
  final def setupFlow(rivers: Seq[RiverSegment]): Unit = {
    val flowInitialiser = new FlowInitialiser(random)
    flowInitialiser.setup(rivers)
  }


  final def removeRiver(river: RiverSegment) = {
    _rivers -= river
    river.removeFromHexes()
  }

  final def addRiver(hexA: Hex, sideA: Side): Option[RiverSegment] = {
    RiverSegment.create(rivers, hexA, sideA).map { r =>
      _rivers += r
      r
    }
  }

}