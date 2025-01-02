package com.easternsauce.game.gamestate.ability.scenario.step

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.entitycreator.AbilityComponentToCreate
import com.easternsauce.game.gamestate.ability.AbilityComponentType.AbilityComponentType
import com.easternsauce.game.gamestate.ability.scenario.AbilityComponentScenarioStepParams
import com.easternsauce.game.gamestate.ability.scenario.NextStepCondition.NextStepCondition

case class GenericScenarioStep(
    abilityComponentType: Option[AbilityComponentType],
    nextStepCondition: NextStepCondition,
    expirationTime: Option[Float]
) extends AbilityScenarioStep {

  override def scheduleComponents(
      scenarioStepParams: AbilityComponentScenarioStepParams
  )(implicit game: CoreGame): Unit = {
    abilityComponentType.foreach(abilityComponentType =>
      game.queues.abilityComponentsToCreate += AbilityComponentToCreate(
        abilityId = scenarioStepParams.abilityId,
        componentType = abilityComponentType,
        currentAreaId = scenarioStepParams.currentAreaId,
        creatureId = scenarioStepParams.creatureId,
        pos = scenarioStepParams.pos,
        facingVector = scenarioStepParams.facingVector,
        damage = scenarioStepParams.damage,
        scenarioStepNo = scenarioStepParams.scenarioStepNo,
        expirationTime = expirationTime
      )
    )
  }
}
