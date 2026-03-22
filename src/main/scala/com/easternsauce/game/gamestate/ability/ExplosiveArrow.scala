package com.easternsauce.game.gamestate.ability
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.scenario.NextStepCondition
import com.easternsauce.game.gamestate.ability.scenario.step.AbilityScenarioStep
import com.easternsauce.game.gamestate.ability.scenario.step.GenericScenarioStep
import com.easternsauce.game.gamestate.ability.scenario.step.ProjectileScenarioStep
import com.easternsauce.game.gamestate.projectile.ProjectileComponentType

case class ExplosiveArrow(params: AbilityParams) extends Ability {

  override def onActiveStart()(implicit game: CoreGame): Ability = {

    this
  }

  override def channelTime: Float = 0.5f

  override def finishWhenComponentsDestroyed: Boolean = true

  override def copy(params: AbilityParams): Ability = ExplosiveArrow(params)

  override def scenarioSteps: List[AbilityScenarioStep] = List(
    ProjectileScenarioStep(
      Some(ProjectileComponentType.ArrowComponent),
      NextStepCondition.ChainCondition,
      None,
      0f,
      0f,
      1
    ),
    ProjectileScenarioStep(
      Some(ProjectileComponentType.GhostArrowComponent),
      NextStepCondition.ChainCondition,
      Some(0.4f),
      -60f,
      15f,
      9
    ),
    GenericScenarioStep(
      Some(ProjectileComponentType.ExplosionComponent),
      NextStepCondition.ChainCondition,
      Some(0.6f)
    ),
    ProjectileScenarioStep(
      Some(ProjectileComponentType.GhostArrowComponent),
      NextStepCondition.ChainCondition,
      Some(0.6f),
      -180f,
      0f,
      1
    ),
    GenericScenarioStep(
      Some(ProjectileComponentType.ExplosionComponent),
      NextStepCondition.ChainCondition,
      Some(0.6f)
    )
  )
}
