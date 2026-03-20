package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.scenario.NextStepCondition
import com.easternsauce.game.gamestate.ability.scenario.step.AbilityScenarioStep
import com.easternsauce.game.gamestate.ability.scenario.step.ProjectileScenarioStep

case class ArrowVolley(params: AbilityParams) extends Ability {

  override def onActiveStart()(implicit game: CoreGame): Ability = {
    this
  }

  override def channelTime: Float = 0.4f

  override def finishWhenComponentsDestroyed: Boolean = true

  override def copy(params: AbilityParams): Ability = ArrowVolley(params)

  override def scenarioSteps: List[AbilityScenarioStep] = List(
    ProjectileScenarioStep(
      abilityComponentType = Some(AbilityComponentType.ArrowComponent),
      nextStepCondition = NextStepCondition.NullCondition,
      expirationTime = None,
      startingAngle = -30f,
      angleBetweenProjectiles = 10f,
      numOfProjectiles = 7
    )
  )
}
