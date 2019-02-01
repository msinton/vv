package com.consideredgames.game.model.game

case class GameState(var turnState: TurnState) {

  def endTurn() = turnState = turnState.turnCompleted

}
