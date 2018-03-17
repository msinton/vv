package com.consideredgames.game.model.hex

import com.consideredgames.game.model.board.{BoardUtils, HexPosition}
import org.scalatest.FunSuite

import scala.util.Random

class RiverNetworkTest extends FunSuite {

  type Result = (Seq[RiverSegment], Seq[HexGroup])

  def createRiverNetwork(numPlayers: Int, random: Random): (Seq[RiverSegment], Seq[HexGroup]) = {
    val hexes = (0 until 21).map(i => Hex(i, HexType.CLAY))

    val hexesByPos = hexes.zipWithIndex.map({
      case (h, i) => (HexPosition(i % 3, i / 3), h)
    }).toMap

    BoardUtils.connectHexes(hexesByPos)
    PointInitialiser.setupPoints(hexes)

    val rn = new RiverNetwork(random)
    rn.generate(hexes, numPlayers)
  }

  def allDistinct[T](sets: Seq[scala.collection.Set[T]]): Boolean = {
    sets.reduce(_ union _).size == sets.map(_.size).sum
  }

  def assertGroupsAreSeparate(n: Int)(result: Result, expectedNum: Int) = test(s"$n: Groups Are Separate") {
    val (_, groups) = result
    assert(groups.size == expectedNum)
    assert(allDistinct(groups))
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

  def assertRiversAreConnected(n: Int)(result: Result, expectedNumGroups: Int) = test(s"$n: Rivers Are Connected") {
    val (rivers, _) = result
    val riverGroups = separateRiverGroups(rivers)

    assert(riverGroups.nonEmpty)
    assert(riverGroups.size <= expectedNumGroups, "rivers should connect up such that there is at most the same " +
      "connected number of distinct rivers as hex groups")
    assert(allDistinct(riverGroups))
  }

  def allChecks(n: Int)(random: Random, numPlayers: Int): Unit = {

    val result = createRiverNetwork(numPlayers, random)

    val expectedNumGroups = if (numPlayers == 1) 2 else numPlayers

    assertGroupsAreSeparate(n)(result, expectedNumGroups)
    assertRiversAreConnected(n)(result, expectedNumGroups)
  }

  (0 to 100).foreach(i => {
    allChecks(i)(new Random(i), numPlayers = 1 + (i % 3))
  })
}