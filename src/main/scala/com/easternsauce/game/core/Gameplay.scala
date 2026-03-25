package com.easternsauce.game.core

import com.easternsauce.game.gamephysics.WorldSimulation
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gameview.GameView

case class Gameplay()(implicit game: CoreGame) {

  var view: GameView = _
  var worldSimulation: WorldSimulation = _
  var keyHeldChecker: KeyHeldChecker = _
  var buttonHeldChecker: ButtonHeldChecker = _
  var gameStateHolder: GameStateContainer = _
  var tiledMapsManager: TiledMapsManager = _

  def init(): Unit = {
    tiledMapsManager = TiledMapsManager()
    tiledMapsManager.init()

    gameStateHolder = GameStateContainer(GameState())
    gameStateHolder.initGameState()

    view = GameView()
    view.init()

    worldSimulation = WorldSimulation()
    worldSimulation.init(tiledMapsManager.tiledMaps)

    keyHeldChecker = KeyHeldChecker()
    keyHeldChecker.init()

    buttonHeldChecker = ButtonHeldChecker()
    buttonHeldChecker.init()
  }

  def updateTimers(delta: Float): Unit = {
    gameStateHolder.updateGameStateTimers(delta)
  }

  def update(areaId: AreaId, delta: Float): Unit = {
    gameStateHolder.updateGameState(areaId, delta)

    worldSimulation.update(areaId)
    view.update(areaId, delta)
  }

  def render(areaId: AreaId, delta: Float): Unit = {
    view.render(areaId, delta)
  }
}
