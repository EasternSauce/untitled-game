package com.easternsauce.game.gamestate.ability.scenario.step

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.scenario.NextStepCondition.NextStepCondition
import com.easternsauce.game.gamestate.ability.scenario.ProjectileComponentScenarioStepParams
import com.easternsauce.game.gamestate.projectile.ProjectileComponentType.ProjectileComponentType

trait AbilityScenarioStep {
  val projectileComponentType: Option[ProjectileComponentType]
  val nextStepCondition: NextStepCondition
  val expirationTime: Option[Float]

  def scheduleComponents(
      scenarioStepParams: ProjectileComponentScenarioStepParams
  )(implicit game: CoreGame): Unit
}
