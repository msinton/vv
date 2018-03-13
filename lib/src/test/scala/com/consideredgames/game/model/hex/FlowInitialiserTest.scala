package com.consideredgames.game.model.hex

import com.consideredgames.game.model.board.{BoardUtils, HexPosition}
import org.scalatest.{FunSuite, OptionValues}

import scala.util.Random

class FlowInitialiserTest extends FunSuite with OptionValues {

  trait hexes {
    val h1 = Hex(1, HexType.CLAY)
    val h2 = Hex(2, HexType.CLAY)
    val h3 = Hex(3, HexType.CLAY)
    val h4 = Hex(4, HexType.CLAY)
    val h5 = Hex(5, HexType.CLAY)

    val hexMap = collection.mutable.AnyRefMap.empty[HexPosition, Hex]
    hexMap.update(HexPosition(1, 1), h1)
    hexMap.update(HexPosition(1, 0), h2)
    hexMap.update(HexPosition(0, 0), h3)
    hexMap.update(HexPosition(2, 0), h4)
    hexMap.update(HexPosition(0, 1), h5)

    BoardUtils.connectHexes(hexMap.toMap)
    PointInitialiser.setupPoints(hexMap.values)
  }

  /**
    * rivers setup - by point ids:
    *
    * 5 - 4 - 3 - 1 - 7
   */
  trait boardSetupFixture extends hexes {
    val rivers = List(
      RiverSegment(h2, Side.southEast),
      RiverSegment(h2, Side.south),
      RiverSegment(h1, Side.northWest),
      RiverSegment(h3, Side.south)
    )
  }

  /**
    * rivers setup - by point ids:
    *
    *         2
    *         |
    * 5 - 4 - 3 - 1 - 7
    */
  trait boardSetupFixtureWithSource extends hexes {
    val rivers = List(
      RiverSegment(h2, Side.southEast),
      RiverSegment(h2, Side.south),
      RiverSegment(h1, Side.northWest),
      RiverSegment(h3, Side.south),
      RiverSegment(h3, Side.northEast)
    )
  }

  /**
    * 5 <- 4 -> 3 -> 1 -> 7
    */
  test("when there is just one dividing river, it should pick a source and flow out on either side") {

    new boardSetupFixture {
      val random = new Random(1)
      val flowInitialiser = new FlowInitialiser(random)
      flowInitialiser.setup(rivers)

      rivers.foreach(r => assert(r.flow.nonEmpty))
      assert(rivers.map(r => (r.flow.get.from.id, r.flow.get.to.id)).toSet == Set(
        (4, 3),
        (3, 1),
        (1, 7),
        (4, 5)
      ))
    }
  }


  /**
    *           2
    *           ^
    *           |
    * 5 <- 4 <- 3 -> 1 -> 7
    *
    */
  test("when there is already a source (three rivers join at one point), it should start from that source") {

    new boardSetupFixtureWithSource {

      val random = new Random(1)
      val initialiser = new FlowInitialiser(random)
      initialiser.setup(rivers)

      rivers.foreach(r => assert(r.flow.nonEmpty))
      assert(rivers.map(r => (r.flow.get.from.id, r.flow.get.to.id)).toSet == Set(
        (3, 1),
        (1, 7),
        (3, 4),
        (4, 5),
        (3, 2)
      ))
    }
  }

}
