package com.easternsauce.game.gamestate.ability
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.scenario.NextStepCondition
import com.easternsauce.game.gamestate.ability.scenario.step.{AbilityScenarioStep, GenericScenarioStep, ProjectileScenarioStep}

case class Arrow(params: AbilityParams) extends Ability {

  override def onActiveStart()(implicit game: CoreGame): Ability = {

    this
  }

  override def channelTime: Float = 0.5f

  override def finishWhenComponentsDestroyed: Boolean = true

  override def copy(params: AbilityParams): Ability = Arrow(params)

  override def scenarioSteps: List[AbilityScenarioStep] = List(
    ProjectileScenarioStep(
      Some(AbilityComponentType.ArrowComponent),
      NextStepCondition.ChainCondition,
      None,
      0f,
      0f,
      1
    ),
    ProjectileScenarioStep(
      Some(AbilityComponentType.GhostArrowComponent),
      NextStepCondition.ChainCondition,
      Some(0.4f),
      -60f,
      15f,
      9
    ),
    GenericScenarioStep(
      Some(AbilityComponentType.ExplosionComponent),
      NextStepCondition.ChainCondition,
      Some(0.6f)
    ),
    ProjectileScenarioStep(
      Some(AbilityComponentType.GhostArrowComponent),
      NextStepCondition.ChainCondition,
      Some(0.6f),
      -180f,
      0f,
      1
    ),
    GenericScenarioStep(
      Some(AbilityComponentType.ExplosionComponent),
      NextStepCondition.ChainCondition,
      Some(0.6f)
    )
  )
}
