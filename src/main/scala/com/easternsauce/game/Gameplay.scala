package com.easternsauce.game

import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamephysics.GamePhysics
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gameview.GameView

case class Gameplay(game: CoreGame) {

  var gameTiledMaps: Map[AreaId, GameTiledMap] = _

  var gameView: GameView = _

  var gamePhysics: GamePhysics = _

  var gameState: GameState = _

  var playersToCreate: List[String] = _

  var keysHeld: Map[Int, Boolean] = Map()

  def init(): Unit = {
    gameTiledMaps = Constants.MapAreaNames
      .map(name => (AreaId(name), GameTiledMap(AreaId(name))))
      .toMap
    gameTiledMaps.values.foreach(_.init())

    gameState = GameState()

    gameView = GameView()
    gameView.init(game)

    gamePhysics = GamePhysics()
    gamePhysics.init(gameTiledMaps, gameState)

    playersToCreate = List()
  }

  def update(delta: Float): Unit = {
    gameState = gameState.update(delta, game)

    val areaId = game.clientCreatureId
      .filter(gameState.creatures.contains(_))
      .map(gameState.creatures(_).currentAreaId)
      .getOrElse(Constants.DefaultAreaId)

    gamePhysics.updateForArea(areaId, gameState)

    gameView.update(delta, game)
    gameView.render(delta, game)
  }

  def schedulePlayerToCreate(clientId: String): Unit = {
    playersToCreate = playersToCreate.appended(clientId)
  }

  def keyHeld(key: Int): Boolean = keysHeld(key)
  def setKeyHeld(key: Int, value: Boolean): Unit = keysHeld =
    keysHeld.updated(key, value)
}
