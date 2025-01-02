package com.easternsauce.game.gamestate.creature.behavior

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.AbilityType
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.event.CreaturePerformAbilityEvent
import com.easternsauce.game.math.Vector2f
import com.softwaremill.quicklens.ModifyPimp

case class EnemyBehavior() extends CreatureBehavior {
  def run()(implicit game: CoreGame): Creature => Creature = { creature =>
    val closestPlayer = getClosestPlayer(creature)

    creature.transformIf(
      creature.isAlive && closestPlayer.nonEmpty && closestPlayer.get.isAlive
    ) {
      pursueTarget(creature, closestPlayer.get)
    }
  }

  private def getClosestPlayer(
      creature: Creature
  )(implicit game: CoreGame): Option[Creature] = {
    game.gameState.creatures.values
      .filter(otherCreature =>
        otherCreature.params.isPlayer && creature.pos.distance(
          otherCreature.pos
        ) < Constants.AggroDistance
      )
      .minByOption(otherCreature => creature.pos.distance(otherCreature.pos))
  }

  private def pursueTarget(creature: Creature, target: Creature)(implicit
      game: CoreGame
  ): Creature = {
    val vectorTowardsTarget =
      creature.pos.vectorTowards(target.pos)

    val distanceToTarget = vectorTowardsTarget.len

    val posInFrontOfTarget = creature.pos.add(
      vectorTowardsTarget.normalized.multiply(
        distanceToTarget - Constants.WalkUpDistance
      )
    )

    if (distanceToTarget <= Constants.AttackDistance) {
      stopAndAttackTarget(creature, target, vectorTowardsTarget)
    } else {
      followTarget(creature, vectorTowardsTarget, posInFrontOfTarget)
    }
  }

  private def followTarget(
      creature: Creature,
      vectorTowardsTarget: Vector2f,
      posInFrontOfTarget: Vector2f
  )(implicit game: CoreGame): Creature = {
    creature
      .modify(_.params.destination)
      .setTo(posInFrontOfTarget)
      .modify(_.params.isDestinationReached)
      .setTo(false)
      .modify(_.params.facingVector)
      .setTo(vectorTowardsTarget)
  }

  private def stopAndAttackTarget(
      creature: Creature,
      target: Creature,
      vectorTowardsTarget: Vector2f
  )(implicit game: CoreGame): Creature = {
    game.sendLocalEvents(
      List(
        CreaturePerformAbilityEvent(
          creature.id,
          creature.currentAreaId,
          AbilityType.MeleeAttack,
          creature.pos,
          target.pos,
          Some(target.id)
        )
      )
    )

    creature
      .modify(_.params.destination)
      .setTo(creature.pos)
      .modify(_.params.isDestinationReached)
      .setTo(true)
      .modify(_.params.facingVector)
      .setTo(vectorTowardsTarget)
  }
}
