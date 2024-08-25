package com.easternsauce.game

import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamephysics.GamePhysics
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.event.GameStateEvent
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gameview.GameView

import scala.collection.mutable

case class Gameplay()(implicit game: CoreGame) {

  var tiledMaps: mutable.Map[AreaId, GameTiledMap] = _
  var view: GameView = _
  var physics: GamePhysics = _
  var gameStateHolder: GameStateHolder = _
  var playersToCreate: List[String] = _
  var keysHeld: mutable.Map[Int, Boolean] = _

  def init(): Unit = {
    tiledMaps = mutable.Map() ++ Constants.MapAreaNames
      .map(name => (AreaId(name), GameTiledMap(AreaId(name))))
      .toMap
    tiledMaps.values.foreach(_.init())

    gameStateHolder = GameStateHolder(GameState())

    view = GameView()
    view.init()

    physics = GamePhysics()
    physics.init(tiledMaps)

    playersToCreate = List()

    keysHeld = mutable.Map()
  }

  def updateForArea(areaId: AreaId, delta: Float): Unit = {
    gameStateHolder.updateGameStateForArea(areaId, delta)
    physics.updateForArea(areaId)
    view.updateForArea(areaId, delta)
    view.renderForArea(areaId, delta)
  }

  def applyEvent(event: GameStateEvent): Unit = {
    gameStateHolder.gameState = event.applyToGameState(gameState)
  }

  def schedulePlayerToCreate(clientId: String): Unit = {
    playersToCreate = playersToCreate.appended(clientId)
  }

  def keyHeld(key: Int): Boolean = keysHeld(key)
  def setKeyHeld(key: Int, value: Boolean): Unit =
    keysHeld.update(key, value)

  def gameState: GameState = gameStateHolder.gameState
}
