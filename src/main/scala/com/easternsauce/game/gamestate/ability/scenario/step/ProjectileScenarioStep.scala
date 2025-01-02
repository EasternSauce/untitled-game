package com.easternsauce.game.gamestate.ability.scenario.step

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.entitycreator.AbilityComponentToCreate
import com.easternsauce.game.gamestate.ability.AbilityComponentType.AbilityComponentType
import com.easternsauce.game.gamestate.ability.scenario.AbilityComponentScenarioStepParams
import com.easternsauce.game.gamestate.ability.scenario.NextStepCondition.NextStepCondition

case class ProjectileScenarioStep(
    abilityComponentType: Option[AbilityComponentType],
    nextStepCondition: NextStepCondition,
    expirationTime: Option[Float],
    startingAngle: Float,
    angleBetweenProjectiles: Float,
    numOfProjectiles: Int
) extends AbilityScenarioStep {

  override def scheduleComponents(
      scenarioStepParams: AbilityComponentScenarioStepParams
  )(implicit game: CoreGame): Unit = {
    abilityComponentType.foreach(abilityComponentType =>
      game.queues.abilityComponentsToCreate ++= (0 until numOfProjectiles).map(
        projectileNum =>
          AbilityComponentToCreate(
            abilityId = scenarioStepParams.abilityId,
            componentType = abilityComponentType,
            currentAreaId = scenarioStepParams.currentAreaId,
            creatureId = scenarioStepParams.creatureId,
            pos = scenarioStepParams.pos,
            facingVector = scenarioStepParams.facingVector.setAngleDeg(
              scenarioStepParams.facingVector.angleDeg + startingAngle + projectileNum * angleBetweenProjectiles
            ),
            damage = scenarioStepParams.damage,
            scenarioStepNo = scenarioStepParams.scenarioStepNo,
            expirationTime = expirationTime
          )
      )
    )
  }
}
