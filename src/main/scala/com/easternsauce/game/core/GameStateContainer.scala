package com.easternsauce.game.core

import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.event.GameStateEvent
import com.easternsauce.game.gamestate.id.AreaId

case class GameStateContainer(private var _gameState: GameState) {

  def initGameState(): Unit = {
    _gameState = _gameState.init()
  }

  def updateGameStateForArea(areaId: AreaId, delta: Float)(implicit
      game: CoreGame
  ): Unit = {
    _gameState = _gameState.updateForArea(areaId, delta)
  }

  def updateGameStateTimers(delta: Float): Unit = {
    _gameState = _gameState.updateTimers(delta)
  }

  def applyGameStateEvents(
      events: List[GameStateEvent]
  )(implicit game: CoreGame): Unit = {
    _gameState = _gameState.applyEvents(events)
  }

  def forceGameStateOverride(overrideGameState: GameState): Unit = {
    _gameState = overrideGameState
  }

  def gameState: GameState = _gameState

}
