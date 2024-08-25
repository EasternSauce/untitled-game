package com.easternsauce.game

import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.id.AreaId

case class GameStateHolder(var gameState: GameState) {
  def updateGameStateForArea(areaId: AreaId, delta: Float)(implicit
      game: CoreGame
  ): Unit = {
    gameState = gameState.updateForArea(areaId, delta)
  }
}
