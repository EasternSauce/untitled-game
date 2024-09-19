package com.easternsauce.game.core

import com.easternsauce.game.Constants
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamephysics.GamePhysics
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.event.GameStateEvent
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gameview.GameView

import scala.collection.mutable

case class Gameplay()(implicit game: CoreGame) {

  private var _tiledMaps: mutable.Map[AreaId, GameTiledMap] = _
  private var _view: GameView = _
  private var _physics: GamePhysics = _
  private var gameStateHolder: GameStateContainer = _
  private var _playersToCreate: mutable.ListBuffer[String] = _
  private var keysHeld: mutable.Map[Int, Boolean] = _

  def init(): Unit = {
    _tiledMaps = mutable.Map() ++ Constants.MapAreaNames
      .map(name => (AreaId(name), GameTiledMap(AreaId(name))))
      .toMap
    _tiledMaps.values.foreach(_.init())

    gameStateHolder = GameStateContainer(GameState())

    _view = GameView()
    _view.init()

    _physics = GamePhysics()
    _physics.init(_tiledMaps)

    _playersToCreate = mutable.ListBuffer()

    keysHeld = mutable.Map()
  }

  def updateForArea(areaId: AreaId, delta: Float): Unit = {
    gameStateHolder.updateForArea(areaId, delta)
    _physics.updateForArea(areaId)
    _view.updateForArea(areaId, delta)
  }

  def renderForArea(areaId: AreaId, delta: Float): Unit = {
    _view.renderForArea(areaId, delta)
  }

  def applyEventsToGameState(events: List[GameStateEvent]): Unit = {
    gameStateHolder.applyEvents(events)
  }

  def schedulePlayerToCreate(clientId: String): Unit = {
    _playersToCreate += clientId
  }

  def keyHeld(key: Int): Boolean = keysHeld.getOrElse(key, false)

  def setKeyHeld(key: Int, value: Boolean): Unit =
    keysHeld.update(key, value)

  def playersToCreate: List[String] = {
    _playersToCreate.toList
  }

  def overrideGameState(gameState: GameState): Unit = {
    gameStateHolder.forceOverride(gameState)
  }

  def clearPlayersToCreate(): Unit = {
    _playersToCreate.clear()
  }

  def gameState: GameState = gameStateHolder.gameState

  def tiledMaps: Map[AreaId, GameTiledMap] = _tiledMaps.toMap

  def view: GameView = _view

  def physics: GamePhysics = _physics
}
