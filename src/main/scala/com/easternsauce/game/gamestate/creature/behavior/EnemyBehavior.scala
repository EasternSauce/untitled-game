package com.easternsauce.game.gamestate.creature.behavior

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.Creature
import com.softwaremill.quicklens.ModifyPimp

case class EnemyBehavior() extends CreatureBehavior {
  def run()(implicit
      game: CoreGame
  ): Creature => Creature = { creature =>
    val closestPlayer = game.gameState.creatures.values
      .filter(otherCreature =>
        otherCreature.params.isPlayer && creature.pos.distance(
          otherCreature.pos
        ) < Constants.AggroDistance
      )
      .minByOption(otherCreature => creature.pos.distance(otherCreature.pos))

    creature.transformIf(creature.isAlive && closestPlayer.nonEmpty) {
      val vectorTowardsTarget =
        creature.pos.vectorTowards(closestPlayer.get.pos)

      val distanceToTarget = vectorTowardsTarget.length

      val posInFrontOfTarget = creature.pos.add(
        vectorTowardsTarget.normalized.multiply(
          distanceToTarget - Constants.WalkUpDistance
        )
      )

      creature
        .modify(_.params.destination)
        .setTo(posInFrontOfTarget)
        .modify(_.params.isDestinationReached)
        .setTo(false)
        .modify(_.params.facingVector)
        .setTo(vectorTowardsTarget)
    }
  }

}
