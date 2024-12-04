package com.easternsauce.game.core

import com.easternsauce.game.gamephysics.GamePhysics
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gameview.GameView

case class Gameplay()(implicit game: CoreGame) {

  var view: GameView = _
  var physics: GamePhysics = _
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

    physics = GamePhysics()
    physics.init(tiledMapsManager.tiledMaps)

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
    physics.update(areaId)
    view.update(areaId, delta)
  }

  def render(areaId: AreaId, delta: Float): Unit = {
    view.render(areaId, delta)
  }

}
