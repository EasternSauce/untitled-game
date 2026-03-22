package com.easternsauce.game.gamestate.ability.scenario

sealed trait AbilityScenarioEvent

case class ProjectileComponentScenarioRunStepEvent(
    scenarioStepParams: ProjectileComponentScenarioStepParams
) extends AbilityScenarioEvent
