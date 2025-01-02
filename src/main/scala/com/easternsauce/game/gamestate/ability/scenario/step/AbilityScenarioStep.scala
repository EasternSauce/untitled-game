package com.easternsauce.game.gamestate.ability.scenario.step

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.AbilityComponentType.AbilityComponentType
import com.easternsauce.game.gamestate.ability.scenario.AbilityComponentScenarioStepParams
import com.easternsauce.game.gamestate.ability.scenario.NextStepCondition.NextStepCondition

trait AbilityScenarioStep {
  val abilityComponentType: Option[AbilityComponentType]
  val nextStepCondition: NextStepCondition
  val expirationTime: Option[Float]

  def scheduleComponents(
      scenarioStepParams: AbilityComponentScenarioStepParams
  )(implicit game: CoreGame): Unit
}
