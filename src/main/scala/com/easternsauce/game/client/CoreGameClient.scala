package com.easternsauce.game.client

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Buttons
import com.easternsauce.game.Constants
import com.easternsauce.game.command.ActionsPerformCommand
import com.easternsauce.game.connectivity.GameClientConnectivity
import com.easternsauce.game.core.{CoreGame, Gameplay}
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.event.{AreaGameStateEvent, CreatureGoToEvent, GameStateEvent, OperationalGameStateEvent}
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.MousePosTransformations
import com.easternsauce.game.screen.gameplay.client.ClientGameplayScreen
import com.easternsauce.game.screen.pausemenu.client.ClientPauseMenuScreen
import com.easternsauce.game.screen.startmenu.client.ClientStartMenuScreen
import com.esotericsoftware.kryonet.Client

case class CoreGameClient() extends CoreGame {
  implicit val game: CoreGame = this

  private var clientRegistered = false

  private var _gameplay: Gameplay = _
  private var _connectivity: GameClientConnectivity = _

  override protected def init(): Unit = {
    gameplayScreen = ClientGameplayScreen(this)
    startMenuScreen = ClientStartMenuScreen(this)
    pauseMenuScreen = ClientPauseMenuScreen(this)

    _gameplay = Gameplay()
    _gameplay.init()

    _connectivity = GameClientConnectivity(this)
  }

  override def update(delta: Float): Unit = {
    val areaId = clientCreatureAreaId.getOrElse(Constants.DefaultAreaId)

    handleInputs()

    gameplay.updateForArea(areaId, delta)
    gameplay.renderForArea(areaId, delta)

    val operationalEvents = broadcastEventsQueue.toList.filter {
      case _: OperationalGameStateEvent => true
      case _                            => false
    }

    gameplay.applyEventsToGameState(operationalEvents)

    broadcastEventsQueue.clear()

    scheduledOverrideGameState.foreach { gameState =>
      gameplay.overrideGameState(gameState)
      clientCreatureAreaId.foreach(gameplay.physics.correctBodyPositions(_))
      scheduledOverrideGameState = None
    }
  }

  def scheduleOverrideGameState(gameState: GameState): Unit = {
    scheduledOverrideGameState = Some(gameState)
  }

  override protected def handleInputs(): Unit = {
    if (gameplay.keyHeld(Buttons.LEFT)) {
      val clientCreature = clientCreatureId
        .filter(gameState.creatures.contains)
        .map(gameState.creatures(_))

      clientCreature.foreach { creature =>
        val destination = MousePosTransformations.mouseWorldPos(
          Gdx.input.getX.toFloat,
          Gdx.input.getY.toFloat,
          creature.pos
        )

        sendEvent(
          CreatureGoToEvent(
            creature.id,
            creature.currentAreaId,
            destination
          )
        )
      }
    }
  }

  override def sendEvent(event: GameStateEvent): Unit = {
    broadcastEventsQueue.addOne(event)
  }

  override def processBroadcastEventsForArea(
      areaId: AreaId,
      gameState: GameState
  ): GameState = {
    val events = broadcastEventsQueue.toList.filter {
      case event: AreaGameStateEvent => event.areaId == areaId
      case _                         => false
    }

    val updatedGameState = gameState.applyEvents(events)

    if (events.nonEmpty) {
      client.sendTCP(ActionsPerformCommand(events))
    }

    updatedGameState
  }

  def registerClient(clientId: String): Unit = {
    clientData.clientId = Some(clientId)
    clientRegistered = true
  }

  override def gameplay: Gameplay = _gameplay
  override protected def connectivity: GameClientConnectivity = _connectivity
  def client: Client = _connectivity.endPoint

  override def dispose(): Unit = {
    super.dispose()
    if (client != null) {
      client.close()
    }
  }

}
