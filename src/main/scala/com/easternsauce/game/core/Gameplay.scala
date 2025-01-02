package com.easternsauce.game.core

import com.easternsauce.game.gamephysics.GamePhysics
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.scenario.{AbilityComponentScenarioRunStepEvent, AbilityComponentScenarioStepParams}
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gameview.GameView

case class Gameplay()(implicit game: CoreGame) {

  var view: GameView = _
  var physics: GamePhysics = _
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

    physics = GamePhysics()
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
    game.queues.abilityScenarioEvents.toList.foreach {
      case event: AbilityComponentScenarioRunStepEvent =>
        scheduleNextScenarioStepComponents(event.scenarioStepParams)
    }

    game.queues.abilityScenarioEvents.clear()
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
