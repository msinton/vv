package com.consideredgames.game.model.hex

import com.consideredgames.game.model.board.{BoardUtils, HexPosition}
import org.scalatest.FunSuite

import scala.util.Random

class RiverNetworkTest extends FunSuite {

  def createRiverNetwork(numPlayers: Int, random: Random): RiverNetwork = {
    val hexes = (0 until 21).map(i => Hex(i, HexType.CLAY))

    val hexesByPos = hexes.zipWithIndex.map({
      case (h, i) => (HexPosition(i % 3, i / 3), h)
    }).toMap

    BoardUtils.connectHexes(hexesByPos)
    PointInitialiser.setupPoints(hexes)

    val rn = new RiverNetwork(random)
    rn.init(hexes, numPlayers)
    rn
  }

  def allDistinct[T](sets: List[scala.collection.Set[T]]): Boolean = {
    sets.reduce(_ union _).size == sets.map(_.size).sum
  }

  def assertGroupsAreSeparate(n: Int)(rn: RiverNetwork, expectedNum: Int) = test(s"$n: Groups Are Separate") {
    val splitGroups = rn.getGroups.toList
    assert(splitGroups.size == expectedNum)
    assert(allDistinct(splitGroups))
  }

  case class RiverGroup(body: Set[RiverSegment] = Set(), heads: Seq[RiverSegment] = Nil)

  def separateRiverGroups(rivers: Seq[RiverSegment], group: Option[RiverGroup] = None, completeGroups: List[RiverGroup] = Nil): List[Set[RiverSegment]] = {
    rivers match {
      case Nil => (group.toList ++ completeGroups).map(g => g.body ++ g.heads)
      case _ =>

        if (group.isEmpty) separateRiverGroups(rivers.tail, Option(RiverGroup(heads = List(rivers.head))), completeGroups)
        else {

          val (newCompleted, newGroup) = group.map(g => {
            val candidates = g.heads.flatMap(_.neighbours().keys)
            val nextConnected = candidates.filter(rivers.contains(_))
            RiverGroup(g.body ++ g.heads, nextConnected)
          }).partition(_.heads.isEmpty)

          val nextRivers = rivers.toSet.diff(newGroup.flatMap(_.heads).toSet).toSeq

          separateRiverGroups(nextRivers, newGroup.headOption, completeGroups ++ newCompleted)
        }
    }
  }

  def assertRiversAreConnected(n: Int)(rn: RiverNetwork, expectedNumGroups: Int) = test(s"$n: Rivers Are Connected") {
    rn.setupFlow()
    val rivers = rn.getRivers

    val riverGroups = separateRiverGroups(rivers)

    assert(riverGroups.nonEmpty)
    assert(riverGroups.size <= expectedNumGroups)
    assert(allDistinct(riverGroups))
  }

  def allChecks(n: Int)(random: Random, numPlayers: Int): Unit = {

    val rn = createRiverNetwork(numPlayers, random)

    val expectedNumGroups = if (numPlayers == 1) 2 else numPlayers

    assertGroupsAreSeparate(n)(rn, expectedNumGroups)
    assertRiversAreConnected(n)(rn, expectedNumGroups)
  }

  (0 to 10).foreach(i => {
    allChecks(i)(new Random(i), numPlayers = 1 + (i % 3))
  })

}
