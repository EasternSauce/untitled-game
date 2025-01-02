package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.scenario.NextStepCondition
import com.easternsauce.game.gamestate.ability.scenario.step.{AbilityScenarioStep, GenericScenarioStep}
import com.easternsauce.game.gamestate.event.MeleeAttackHitsCreatureEvent
import com.softwaremill.quicklens.ModifyPimp

case class MeleeAttack(params: AbilityParams) extends Ability {

  override def onActiveStart()(implicit game: CoreGame): Ability = {
    params.targetId.foreach(targetId => {
      if (
        game.gameState.creatures.contains(targetId) && game.gameState.creatures
          .contains(params.creatureId)
      ) {
        val sourceCreature = game.gameState.creatures(params.creatureId)
        val targetCreature = game.gameState.creatures(targetId)

        if (
          targetCreature.pos.distance(
            sourceCreature.pos
          ) < range + Constants.MeleeAttackRangeForgiveness
        ) {
          game.queues.localEvents += MeleeAttackHitsCreatureEvent(
            targetId,
            id,
            params.currentAreaId
          )
        }

      }
    })

    this
      .asInstanceOf[Ability]
      .modify(_.params.state)
      .setTo(AbilityState.Finished)
  }

  override def channelTime: Float = 0.5f

  override def range: Float = 2f

  override def isMelee: Boolean = true

  override def copy(params: AbilityParams): Ability = MeleeAttack(params)

  override def scenarioSteps: List[AbilityScenarioStep] = List(
    GenericScenarioStep(
      None,
      NextStepCondition.NullCondition,
      None
    )
  )
}
