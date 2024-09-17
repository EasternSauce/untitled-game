package com.easternsauce.game.core

import com.badlogic.gdx.{Game, Gdx}
import com.easternsauce.game.connectivity.GameConnectivity
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.event.GameStateEvent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.gameview.GameScreen
import com.esotericsoftware.kryonet.Listener

abstract class CoreGame extends Game {

  protected var gameEventProcessor: GameEventProcessor = _

  protected var scheduledOverrideGameState: Option[GameState] = _

  var clientData: ClientData = _

  protected def init(): Unit

  override def create(): Unit = {
    init()

    gameplayScreen.init()
    startMenuScreen.init()
    pauseMenuScreen.init()

    clientData = ClientData()

    gameEventProcessor = GameEventProcessor()
    gameEventProcessor.init()

    scheduledOverrideGameState = None

    setScreen(startMenuScreen)
  }

  def update(delta: Float): Unit

  protected def handleInputs(): Unit

  def clientCreatureId: Option[GameEntityId[Creature]] = {
    clientData.clientId.map(GameEntityId[Creature])
  }

  def clientCreatureAreaId: Option[AreaId] = {
    clientCreatureId
      .filter(gameState.creatures.contains(_))
      .map(gameState.creatures(_))
      .map(_.currentAreaId)
  }

  def gameState: GameState = gameplay.gameState

  def listener: Listener = {
    connectivity.listener
  }

  def processBroadcastEventsForArea(
      area: AreaId,
      gameState: GameState
  ): GameState

  def sendBroadcastEvents(
      events: List[GameStateEvent]
  ): Unit // TODO: does it duplicate applyEvent?

  def sendLocalEvents(events: List[GameStateEvent]): Unit = {
    gameEventProcessor.sendLocalEvents(events)
  }

  def applyEventsToGameState(events: List[GameStateEvent]): Unit = {
    gameplay.applyEventsToGameState(events)
  }

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

  protected def connectivity: GameConnectivity
  def gameplay: Gameplay

  protected def gameplayScreen: GameScreen
  protected def startMenuScreen: GameScreen
  protected def pauseMenuScreen: GameScreen

  def close(): Unit = {
    Gdx.app.exit()
  }

}
