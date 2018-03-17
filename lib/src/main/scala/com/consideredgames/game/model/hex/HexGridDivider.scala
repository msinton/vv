package com.consideredgames.game.model.hex

import com.consideredgames.common.Utils

import scala.annotation.tailrec
import scala.util.Random

class HexGridDivider(random: Random) {

  def divideIntoRoughlyEqualRandomlyShaped(hexes: IndexedSeq[Hex], numGroups: Int): Seq[HexGroup] = {

    type Consumed = Set[Hex]
    type Edges = IndexedSeq[Hex]

    def extend(edges: Edges, maxToAdd: Int, consumed: Consumed): Edges = {
      val hex = Utils.sample(edges, random)
      val unassignedNeighbours = hex.toIndexedSeq.flatMap(_.neighbours.values.toSet.diff(consumed))
      Utils.sample(unassignedNeighbours, random, maxToAdd)
    }

    def isEdge(hex: Hex, consumed: Consumed): Boolean =
      hex.neighbours.values.toSet.diff(consumed).nonEmpty

    def growExtensions(edges: Seq[Edges], increasesDesired: Seq[Int], consumed: Consumed): (Seq[Edges], Consumed) = {

      val (nextExtendedSeq, newConsumedSeq) = edges.zip(increasesDesired)
        .scanLeft((IndexedSeq.empty[Hex], consumed))({
          case ((_, accConsumed), (edge, increase)) =>
            val extended = extend(edge, increase, accConsumed)
            val newConsumed = accConsumed ++ extended
            (extended, newConsumed)
        }).unzip

      (nextExtendedSeq.tail, newConsumedSeq.last)
    }

    @tailrec
    def loop(groups: Seq[HexGroup], edges: Seq[Edges], consumed: Consumed): Seq[HexGroup] = {
      if (consumed.size == hexes.size) {
        groups
      } else {
        val desiredIncreases = groups.map(_.size - groups.map(_.size).min + 3)
        val (extensions, newConsumed) = growExtensions(edges, desiredIncreases, consumed)

        val newGroups = (groups zip extensions).map(x => x._1 ++ x._2)
        // might be more efficient to just do this for the groups rather than zip again?
        val newEdges = (edges zip extensions).map(x => x._1 ++ x._2).map(_.filter(isEdge(_, newConsumed)))
        loop(newGroups, newEdges, newConsumed)
      }
    }

    val groupSeeds = Utils.sample(hexes, random, numGroups)

    loop(
      groups = groupSeeds.map(Set(_)),
      edges = groupSeeds.map(IndexedSeq(_)),
      consumed = groupSeeds.toSet
    )
  }

}