package com.easternsauce.game

import com.badlogic.gdx.Gdx
import com.easternsauce.game.connectivity.GameConnectivity
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamephysics.GamePhysics
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.event.GameStateEvent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.gameview.GameView
import com.esotericsoftware.kryonet.Listener

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

abstract class CoreGame extends ScreenSwitchableGame {

  protected val connectivity: GameConnectivity

  protected var broadcastEventsQueue: ListBuffer[GameStateEvent] = _

  override def create(): Unit = {
    init()

    gameplayScreen.init()
    startMenuScreen.init()
    pauseMenuScreen.init()

    clientData = ClientData()

    broadcastEventsQueue = ListBuffer()

    setScreen(startMenuScreen)
  }

  protected def gameplay: Gameplay

  def update(delta: Float): Unit

  protected def handleInputs(): Unit

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

  def listener: Listener = {
    connectivity.listener
  }

  def playersToCreate: List[String] = {
    gameplay.playersToCreate.toList
  }

  def clearPlayersToCreate(): Unit = {
    gameplay.playersToCreate.clear()
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

  def processBroadcastEventsForArea(
      area: AreaId,
      gameState: GameState
  ): GameState

  def sendEvent(
      event: GameStateEvent
  ): Unit // TODO: does it duplicate applyEvent?

  def applyEventsToGameState(events: List[GameStateEvent]): Unit =
    gameplay.applyEventsToGameState(events)

  override def schedulePlayerToCreate(clientId: String): Unit = {
    gameplay.schedulePlayerToCreate(clientId)
  }

}
