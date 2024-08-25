package com.easternsauce.game

import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamephysics.GamePhysics
import com.easternsauce.game.gamestate.GameState
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

    gameStateHolder = GameStateHolder()
    gameStateHolder.gameState = GameState()

    view = GameView()
    view.init()

    physics = GamePhysics()
    physics.init(tiledMaps)

    playersToCreate = List()

    keysHeld = mutable.Map()
  }

  def update(delta: Float): Unit = {
    gameStateHolder.updateGameState(delta)

    val areaId = game.clientCreatureAreaId.getOrElse(Constants.DefaultAreaId)

    physics.updateForArea(areaId)
    view.updateForArea(areaId, delta)
    view.renderForArea(areaId, delta)
  }

  def schedulePlayerToCreate(clientId: String): Unit = {
    playersToCreate = playersToCreate.appended(clientId)
  }

  def keyHeld(key: Int): Boolean = keysHeld(key)
  def setKeyHeld(key: Int, value: Boolean): Unit =
    keysHeld.update(key, value)

  def gameState: GameState = gameStateHolder.gameState
}
