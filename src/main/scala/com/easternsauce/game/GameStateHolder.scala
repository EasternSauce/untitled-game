package com.easternsauce.game

import com.easternsauce.game.gamestate.GameState

case class GameStateHolder() {
  var gameState: GameState = _

  def updateGameState(delta: Float)(implicit game: CoreGame): Unit = {
    gameState = gameState.update(delta)
  }
}
