package com.easternsauce.game.gamestate.ability.scenario

sealed trait AbilityScenarioEvent

case class AbilityComponentScenarioRunStepEvent(
    scenarioStepParams: AbilityComponentScenarioStepParams
) extends AbilityScenarioEvent
