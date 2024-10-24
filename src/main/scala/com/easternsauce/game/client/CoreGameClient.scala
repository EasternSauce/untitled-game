package com.easternsauce.game.client

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.{Buttons, Keys}
import com.easternsauce.game.Constants
import com.easternsauce.game.command.ActionsPerformRequestCommand
import com.easternsauce.game.connectivity.GameClientConnectivity
import com.easternsauce.game.core.{CoreGame, Gameplay}
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.AbilityType
import com.easternsauce.game.gamestate.event._
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gameview.GameScreen
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

  private var _gameplayScreen: GameScreen = _
  private var _startMenuScreen: GameScreen = _
  private var _pauseMenuScreen: GameScreen = _

  override protected def init(): Unit = {
    _gameplayScreen = ClientGameplayScreen(this)
    _startMenuScreen = ClientStartMenuScreen(this)
    _pauseMenuScreen = ClientPauseMenuScreen(this)

    _gameplay = Gameplay()
    _gameplay.init()

    _connectivity = GameClientConnectivity(this)
  }

  override def update(delta: Float): Unit = {
    val areaId = clientCreatureAreaId.getOrElse(Constants.DefaultAreaId)

    handleInputs()

    gameplay.updateTimers(delta)
    gameplay.updateForArea(areaId, delta)
    gameplay.renderForArea(areaId, delta)

    processEvents(areaId)

    processGameStateOverride()
  }

  private def processEvents(areaId: AreaId): Unit = {
    val broadcastEvents =
      game.queues.broadcastEvents.toList.filter {
        case event: AreaGameStateEvent => event.areaId == areaId
        case _                         => false
      }

    if (broadcastEvents.nonEmpty) {
      client.sendTCP(ActionsPerformRequestCommand(broadcastEvents))
    }

    gameplay.gameStateHolder.applyEvents(
      game.queues.localEvents.toList.filter {
        case event: AreaGameStateEvent    => event.areaId == areaId
        case _: OperationalGameStateEvent => true
        case _                            => false
      }
    )

    game.queues.broadcastEvents.clear()
    game.queues.localEvents.clear()
  }

  private def processGameStateOverride(): Unit = {
    scheduledOverrideGameState.foreach { gameState =>
      gameplay.gameStateHolder.forceOverride(gameState)
      clientCreatureAreaId.foreach(gameplay.physics.correctBodyPositions(_))
      scheduledOverrideGameState = None
    }
  }

  def scheduleOverrideGameState(gameState: GameState): Unit = {
    scheduledOverrideGameState = Some(gameState)
  }

  override protected def handleInputs(): Unit = {
    if (gameplay.keyHeldChecker.keyHeld(Buttons.LEFT)) {
      val clientCreature = clientCreatureId
        .filter(gameState.creatures.contains)
        .map(gameState.creatures(_))

      clientCreature.foreach { creature =>
        val destination = MousePosTransformations.mouseWorldPos(
          Gdx.input.getX.toFloat,
          Gdx.input.getY.toFloat,
          creature.pos
        )

        sendBroadcastEvents(
          List(
            CreatureGoToEvent(
              creature.id,
              creature.currentAreaId,
              destination
            )
          )
        )
      }
    }
    if (gameplay.keyHeldChecker.keyHeld(Keys.SPACE)) {
      val clientCreature = clientCreatureId
        .filter(gameState.creatures.contains)
        .map(gameState.creatures(_))

      clientCreature.foreach { creature =>
        if (
          !creature.params
            .abilityCooldownTimers(AbilityType.Arrow)
            .running || creature.params
            .abilityCooldownTimers(AbilityType.Arrow)
            .time > 1f
        ) {
          sendBroadcastEvents(
            List(
              CreaturePerformAbilityEvent(
                creature.id,
                creature.currentAreaId,
                AbilityType.Arrow,
                creature.pos,
                creature.params.facingVector
              )
            )
          )
        }
      }
    }
  }

  override def sendBroadcastEvents(events: List[GameStateEvent]): Unit = {
    game.queues.broadcastEvents ++= events
  }

  def registerClient(clientId: String): Unit = {
    clientData.clientId = Some(clientId)
    clientRegistered = true
  }

  override def gameplay: Gameplay = _gameplay
  override protected def connectivity: GameClientConnectivity = _connectivity
  def client: Client = _connectivity.endPoint

  override protected def gameplayScreen: GameScreen = _gameplayScreen
  override protected def startMenuScreen: GameScreen = _startMenuScreen
  override protected def pauseMenuScreen: GameScreen = _pauseMenuScreen

  override def dispose(): Unit = {
    super.dispose()
    if (client != null) {
      client.close()
    }
  }

}
