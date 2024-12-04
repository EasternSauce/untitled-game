package com.easternsauce.game.core

import com.badlogic.gdx.{Game, Gdx}
import com.easternsauce.game.connectivity.GameConnectivity
import com.easternsauce.game.entitycreator.PlayerToCreate
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.event.GameStateEvent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.gameview.GameScreen
import com.easternsauce.game.queues.GameQueues
import com.esotericsoftware.kryonet.Listener

abstract class CoreGame extends Game {

  protected var scheduledOverrideGameState: Option[GameState] = _

  var clientData: ClientData = _

  var queues: GameQueues = _

  protected def init(): Unit

  override def create(): Unit = {
    init()

    gameplayScreen.init()
    startMenuScreen.init()
    pauseMenuScreen.init()

    clientData = ClientData()

    queues = GameQueues()

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

  def gameState: GameState = gameplay.gameStateHolder.gameState

  def listener: Listener = {
    connectivity.listener
  }

  def sendBroadcastEvents(
      events: List[GameStateEvent]
  ): Unit

  def sendLocalEvents(
      events: List[GameStateEvent]
  )(implicit game: CoreGame): Unit = {
    game.queues.localEvents ++= events
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

    clientData.clientId.foreach(clientId =>
      queues.playersToCreate += PlayerToCreate(clientId)
    )
  }

  def setPauseScreen(): Unit = {
    setScreen(pauseMenuScreen)
  }

  def setGameplayScreen(): Unit = {
    setScreen(gameplayScreen)
  }

  def gameplay: Gameplay
  protected def connectivity: GameConnectivity

  protected def gameplayScreen: GameScreen
  protected def startMenuScreen: GameScreen
  protected def pauseMenuScreen: GameScreen

  def close(): Unit = {
    Gdx.app.exit()
  }

}
