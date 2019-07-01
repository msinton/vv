package com.consideredgames.game.model.hex

import com.consideredgames.game.model.person.Person

case class Boat(hexA: Hex, sideA: Side) extends BordersHex {

  private var people_ : List[Person] = List()

  var capacity = 1

  def addPerson(person: Person): Boolean =
    if (canAddPerson(person)) {
      people_ = person :: people_
      true
    } else {
      false
    }

  def canAddPerson(person: Person) =
    if (people_.size < capacity)
      people_.exists(_.playerColour == person.playerColour)
    else
      false

  def removePerson(person: Person) = people_ = people_.filterNot(_ == person)

  def people = people_
}
