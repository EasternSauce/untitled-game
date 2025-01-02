package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.scenario.step.AbilityScenarioStep

case class ComplexAbility() extends Ability {

  override val params: AbilityParams = ???

  override def onActiveStart()(implicit game: CoreGame): Ability = ???

  override def channelTime: Float = ???

  override def scenarioSteps: List[AbilityScenarioStep] = List(
//    ProjectileScenarioStep(Some(AbilityComponentType.ArrowComponent), NextStepCondition.ChainCondition, 0f),
//    ProjectileScenarioStep(Some(AbilityComponentType.ArrowComponent), NextStepCondition.ChainCondition, 0f),
//    AreaSplashScenarioStep(Some(AbilityComponentType.ExplosionComponent), NextStepCondition.NullCondition)
  )

  override def copy(params: AbilityParams): Ability = ???
}
