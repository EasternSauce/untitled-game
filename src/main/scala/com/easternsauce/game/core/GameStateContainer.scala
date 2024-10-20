package com.easternsauce.game.core

import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.event.GameStateEvent
import com.easternsauce.game.gamestate.id.AreaId

case class GameStateContainer(private var _gameState: GameState) {
  def updateForArea(areaId: AreaId, delta: Float)(implicit
      game: CoreGame
  ): Unit = {
    _gameState = _gameState.updateForArea(areaId, delta)
  }

  def updateTimers(delta: Float): Unit = {
    _gameState = _gameState.updateTimers(delta)

  }

  def applyEvents(
      events: List[GameStateEvent]
  )(implicit game: CoreGame): Unit = {
    _gameState = _gameState.applyEvents(events)
  }

  def forceOverride(overrideGameState: GameState): Unit = {
    _gameState = overrideGameState
  }

  def gameState: GameState = _gameState

}
