package com.easternsauce.game.core

import com.easternsauce.game.gamephysics.GamePhysics
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gameview.GameView

case class Gameplay()(implicit game: CoreGame) {

  var view: GameView = _
  var physics: GamePhysics = _
  var playersToCreateScheduler: PlayersToCreateScheduler = _
  var keyHeldChecker: KeyHeldChecker = _
  var gameStateHolder: GameStateContainer = _
  var tiledMapsManager: TiledMapsManager = _

  def init(): Unit = {
    tiledMapsManager = TiledMapsManager()
    tiledMapsManager.init()

    gameStateHolder = GameStateContainer(GameState())

    view = GameView()
    view.init()

    physics = GamePhysics()
    physics.init(tiledMapsManager.tiledMaps)

    playersToCreateScheduler = PlayersToCreateScheduler()
    playersToCreateScheduler.init()

    keyHeldChecker = KeyHeldChecker()
    keyHeldChecker.init()
  }

  def updateForArea(areaId: AreaId, delta: Float): Unit = {
    gameStateHolder.updateForArea(areaId, delta)
    physics.updateForArea(areaId)
    view.updateForArea(areaId, delta)
  }

  def renderForArea(areaId: AreaId, delta: Float): Unit = {
    view.renderForArea(areaId, delta)
  }

}
