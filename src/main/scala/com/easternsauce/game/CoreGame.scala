package com.easternsauce.game

import com.badlogic.gdx.{Game, Gdx}
import com.easternsauce.game.connectivity.GameConnectivity
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamephysics.GamePhysics
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.event.GameStateEvent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.gameview.{GameScreen, GameView}
import com.esotericsoftware.kryonet.Listener

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

abstract class CoreGame extends Game {

  protected var gameplayScreen: GameScreen = _
  protected var startMenuScreen: GameScreen = _
  protected var pauseMenuScreen: GameScreen = _

  var clientData: ClientData = _

  protected var broadcastEventsQueue: ListBuffer[GameStateEvent] = _

  protected def init(): Unit

  override def create(): Unit = {
    init()

    gameplayScreen.init()
    startMenuScreen.init()
    pauseMenuScreen.init()

    clientData = ClientData()

    broadcastEventsQueue = ListBuffer()

    setScreen(startMenuScreen)
  }

  protected def connectivity: GameConnectivity
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

  def setClientData(clientId: String, host: String, port: String): Unit = {
    if (clientId.nonEmpty) {
      clientData.clientId = Some(clientId)
    }
    if (host.nonEmpty) {
      clientData.host = Some(host)
    }
    if (port.nonEmpty) {
      clientData.port = Some(port)
    }

    clientData.clientId.foreach(schedulePlayerToCreate)
  }

  def setPauseScreen(): Unit = {
    setScreen(pauseMenuScreen)
  }

  def setGameplayScreen(): Unit = {
    setScreen(gameplayScreen)
  }

  def schedulePlayerToCreate(clientId: String): Unit = {
    gameplay.schedulePlayerToCreate(clientId)
  }

}
