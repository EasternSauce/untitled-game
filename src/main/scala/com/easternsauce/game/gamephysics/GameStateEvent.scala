package com.easternsauce.game.gamephysics

import com.easternsauce.game.gamestate.GameState

trait GameStateEvent {
  def applyToGameState(gameState: GameState): GameState
}
