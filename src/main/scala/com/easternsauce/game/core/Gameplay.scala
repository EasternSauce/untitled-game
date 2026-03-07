package com.easternsauce.game.core

import com.easternsauce.game.gamephysics.WorldSimulation
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.scenario.AbilityComponentScenarioRunStepEvent
import com.easternsauce.game.gamestate.ability.scenario.AbilityComponentScenarioStepParams
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gameview.GameView

case class Gameplay()(implicit game: CoreGame) {

  var view: GameView = _
  var physics: WorldSimulation = _
  var keyHeldChecker: KeyHeldChecker = _
  var buttonHeldChecker: ButtonHeldChecker = _
  var gameStateHolder: GameStateContainer = _
  var tiledMapsManager: TiledMapsManager = _

  def init(): Unit = {
    tiledMapsManager = TiledMapsManager()
    tiledMapsManager.init()

    gameStateHolder = GameStateContainer(GameState())
    gameStateHolder.initGameState()

    view = GameView()
    view.init()

    physics = WorldSimulation()
    physics.init(tiledMapsManager.tiledMaps)

    keyHeldChecker = KeyHeldChecker()
    keyHeldChecker.init()

    buttonHeldChecker = ButtonHeldChecker()
    buttonHeldChecker.init()
  }

  def updateTimers(delta: Float): Unit = {
    gameStateHolder.updateGameStateTimers(delta)
  }

  def update(areaId: AreaId, delta: Float): Unit = {
    gameStateHolder.updateGameState(areaId, delta)
    updateAbilityScenarios()
    physics.update(areaId)
    view.update(areaId, delta)
  }

  private def updateAbilityScenarios(): Unit = {
    // drain() returns all items and clears the queue atomically
    val events = game.queues.abilityScenarioEventQueue.drain()

    events.foreach {
      case event: AbilityComponentScenarioRunStepEvent =>
        scheduleNextScenarioStepComponents(event.scenarioStepParams)
      case _ => // ignore other events if any
    }
  }

  private def scheduleNextScenarioStepComponents(
      scenarioStepParams: AbilityComponentScenarioStepParams
  )(implicit game: CoreGame): Unit = {
    if (game.gameState.abilities.contains(scenarioStepParams.abilityId)) {
      val ability = game.gameState.abilities(scenarioStepParams.abilityId)

      if (scenarioStepParams.scenarioStepNo < ability.scenarioSteps.length) {
        val scenarioStep =
          ability.scenarioSteps(scenarioStepParams.scenarioStepNo)

        scenarioStep.scheduleComponents(scenarioStepParams)
      }
    }
  }

  def render(areaId: AreaId, delta: Float): Unit = {
    view.render(areaId, delta)
  }

}
