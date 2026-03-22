package com.easternsauce.game.gamestate.ability.scenario.step

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.entitycreator.ProjectileComponentToCreate
import com.easternsauce.game.gamestate.ability.scenario.NextStepCondition.NextStepCondition
import com.easternsauce.game.gamestate.ability.scenario.ProjectileComponentScenarioStepParams
import com.easternsauce.game.gamestate.projectile.ProjectileComponentType.ProjectileComponentType

case class ProjectileScenarioStep(
    projectileComponentType: Option[ProjectileComponentType],
    nextStepCondition: NextStepCondition,
    expirationTime: Option[Float],
    startingAngle: Float,
    angleBetweenProjectiles: Float,
    numOfProjectiles: Int
) extends AbilityScenarioStep {

  override def scheduleComponents(
      scenarioStepParams: ProjectileComponentScenarioStepParams
  )(implicit game: CoreGame): Unit = {
    projectileComponentType.foreach { projectileComponentType =>
      val components = (0 until numOfProjectiles).map { projectileNum =>
        ProjectileComponentToCreate(
          abilityId = scenarioStepParams.abilityId,
          componentType = projectileComponentType,
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
      }
      game.queues.projectileComponentQueue.enqueueAll(components)
    }
  }
}
