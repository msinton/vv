package com.consideredgames.game.model.board

import com.consideredgames.game.model.animals.AnimalInfo
import org.scalatest.FunSuite

import scala.util.Random

class BoardDataTest extends FunSuite {

  def assertHexesByPositionNumber(n: Int)(numPlayers: Int, random: Random) = test(s"$n: hexes by position has correct number of hexes," +
    s" with $numPlayers players") {
    val board = new BoardData(numPlayers, random, AnimalInfo.importFromFile().get)

    assert(board.hexes.size > 30)
    assert(board.hexesByPosition.size == board.hexes.size)
  }

  // TODO fix 5!
  (1 to 5).foreach(assertHexesByPositionNumber(0)(_, new Random(1)))

  (1 to 20).foreach(assertHexesByPositionNumber(_)(3, new Random()))

}
