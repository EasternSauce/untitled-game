package com.easternsauce.game.core

import com.easternsauce.game.gamephysics.WorldSimulation
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.Ability
import com.easternsauce.game.gamestate.ability.scenario.ProjectileComponentScenarioRunStepEvent
import com.easternsauce.game.gamestate.ability.scenario.ProjectileComponentScenarioStepParams
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gameview.GameView

case class Gameplay()(implicit game: CoreGame) {

  var view: GameView = _
  var worldSimulation: WorldSimulation = _
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

    worldSimulation = WorldSimulation()
    worldSimulation.init(tiledMapsManager.tiledMaps)

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

    // Log ability components count every frame
    println(
      s"[DEBUG] Frame update: ${gameStateHolder.gameState.projectileComponents.size} ability components in game state, " +
        s"${gameStateHolder.gameState.abilities.size} abilities"
    )
    updateAbilityScenarios()
    worldSimulation.update(areaId)
    view.update(areaId, delta)
  }

  private def updateAbilityScenarios(): Unit = {
    val events = game.queues.abilityScenarioEventQueue.drain()

    events.foreach {
      case event: ProjectileComponentScenarioRunStepEvent =>
        println(
          s"[DEBUG] Running scenario step for ability ${event.scenarioStepParams.abilityId}, step ${event.scenarioStepParams.scenarioStepNo}"
        )
        scheduleNextScenarioStepComponents(event.scenarioStepParams)
      case other =>
        println(s"[DEBUG] Ignored non-step event: $other")
    }
  }

  private def scheduleNextScenarioStepComponents(
      scenarioStepParams: ProjectileComponentScenarioStepParams
  )(implicit game: CoreGame): Unit = {

    if (!game.gameState.abilities.contains(scenarioStepParams.abilityId)) {
      println(s"[DEBUG] Ability ${scenarioStepParams.abilityId} not found, skipping scenario step")
      return
    }

    val ability: Ability = game.gameState.abilities(scenarioStepParams.abilityId)

    if (scenarioStepParams.scenarioStepNo >= ability.scenarioSteps.length) {
      println(
        s"[DEBUG] Scenario step number ${scenarioStepParams.scenarioStepNo} exceeds steps for ability ${scenarioStepParams.abilityId}"
      )
      return
    }

    val scenarioStep = ability.scenarioSteps(scenarioStepParams.scenarioStepNo)
    println(
      s"[DEBUG] Scheduling components for ability ${scenarioStepParams.abilityId}, step ${scenarioStepParams.scenarioStepNo}"
    )
    scenarioStep.scheduleComponents(scenarioStepParams)
  }

  def render(areaId: AreaId, delta: Float): Unit = {
    view.render(areaId, delta)
  }
}
