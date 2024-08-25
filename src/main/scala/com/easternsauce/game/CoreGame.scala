package com.easternsauce.game

import com.badlogic.gdx.Gdx
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamephysics.GamePhysics
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.event.GameStateEvent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.gameview.GameView
import com.esotericsoftware.kryonet.EndPoint

import scala.collection.mutable

abstract class CoreGame extends ScreenSwitchableGame {

  protected val endPoint: EndPoint

  override def create(): Unit = {
    init()

    gameplayScreen.init()
    startMenuScreen.init()
    pauseMenuScreen.init()

    clientData = ClientData()

    setScreen(startMenuScreen)
  }

  protected def gameplay: Gameplay

  def update(delta: Float): Unit = {
    val areaId = clientCreatureAreaId.getOrElse(Constants.DefaultAreaId)

    gameplay.updateForArea(areaId, delta)
  }

  def close(): Unit = {
    Gdx.app.exit()
  }

  def clientCreatureId: Option[GameEntityId[Creature]] = {
    clientData.clientId.map(GameEntityId[Creature])
  }

  def clientCreatureAreaId: Option[AreaId] = {
    clientCreatureId
      .filter(gameState.creatures.contains(_))
      .map(gameState.creatures(_))
      .map(_.currentAreaId)
  }

  def view: GameView = {
    gameplay.view
  }

  def physics: GamePhysics = {
    gameplay.physics
  }

  def gameState: GameState = {
    gameplay.gameState
  }

  def playersToCreate: List[String] = {
    gameplay.playersToCreate
  }

  def clearPlayersToCreate(): Unit = {
    gameplay.playersToCreate = List()
  }

  def tiledMaps: mutable.Map[AreaId, GameTiledMap] = {
    gameplay.tiledMaps
  }

  def keyHeld(key: Int): Boolean = {
    gameplay.keysHeld.getOrElse(key, false)
  }

  def setKeyHeld(key: Int, value: Boolean): Unit = {
    gameplay.setKeyHeld(key, value)
  }

  def applyEvent(event: GameStateEvent): Unit = gameplay.applyEvent(event)

  override def schedulePlayerToCreate(clientId: String): Unit = {
    gameplay.schedulePlayerToCreate(clientId)
  }
}
