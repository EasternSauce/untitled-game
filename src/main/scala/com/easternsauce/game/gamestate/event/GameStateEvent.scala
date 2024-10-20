package com.easternsauce.game.gamestate.event

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.id.AreaId

sealed trait GameStateEvent {
  def applyToGameState(gameState: GameState)(implicit game: CoreGame): GameState
}

trait AreaGameStateEvent extends GameStateEvent {
  val areaId: AreaId
}

trait OperationalGameStateEvent extends GameStateEvent
