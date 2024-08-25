package com.easternsauce.game.gamestate.event

import com.easternsauce.game.gamestate.GameState

trait GameStateEvent {
  def applyToGameState(gameState: GameState): GameState
}
