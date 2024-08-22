package com.easternsauce.game

import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamephysics.GamePhysics
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gameview.GameView

case class Gameplay()(implicit game: CoreGame) {

  var tiledMaps: Map[AreaId, GameTiledMap] = _
  var view: GameView = _
  var physics: GamePhysics = _
  var gameState: GameState = _
  var playersToCreate: List[String] = _
  var keysHeld: Map[Int, Boolean] = _

  def init(): Unit = {
    tiledMaps = Constants.MapAreaNames
      .map(name => (AreaId(name), GameTiledMap(AreaId(name))))
      .toMap
    tiledMaps.values.foreach(_.init())

    gameState = GameState()

    view = GameView()
    view.init()

    physics = GamePhysics()
    physics.init(tiledMaps, gameState)

    playersToCreate = List()

    keysHeld = Map()
  }

  def update(delta: Float): Unit = {
    gameState = gameState.update(delta)

    physics.updateForArea(
      game.clientCreatureAreaId.getOrElse(Constants.DefaultAreaId),
      gameState
    )

    view.update(delta)
    view.render(delta)
  }

  def schedulePlayerToCreate(clientId: String): Unit = {
    playersToCreate = playersToCreate.appended(clientId)
  }

  def keyHeld(key: Int): Boolean = keysHeld(key)
  def setKeyHeld(key: Int, value: Boolean): Unit = keysHeld =
    keysHeld.updated(key, value)
}
