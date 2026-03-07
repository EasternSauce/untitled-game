package com.easternsauce.game.entitycreator

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability._
import com.easternsauce.game.gamestate.id.GameEntityId
import com.softwaremill.quicklens.ModifyPimp

trait AbilityCreator {
  this: GameState =>

  private[entitycreator] def createAbilities(implicit
      game: CoreGame
  ): GameState = {

    // drain the queue to get a Seq[AbilityToCreate] and empty the queue
    val abilitiesToCreate = game.queues.abilityQueue.drain()

    val result = abilitiesToCreate.foldLeft(this) {
      case (gameState, abilityToCreate: AbilityToCreate) =>
        val abilityId = GameEntityId[Ability](
          "ability" + (Math.random() * 1000000).toInt
        )

        val params = AbilityParams(
          id = abilityId,
          currentAreaId = abilityToCreate.currentAreaId,
          creatureId = abilityToCreate.creatureId,
          abilityType = abilityToCreate.abilityType,
          state = AbilityState.Channelling,
          pos = abilityToCreate.pos,
          facingVector = abilityToCreate.facingVector,
          targetId = abilityToCreate.targetId
        )

        val ability = abilityToCreate.abilityType match {
          case AbilityType.Arrow       => Arrow(params)
          case AbilityType.MeleeAttack => MeleeAttack(params)
          case other =>
            throw new RuntimeException(s"Incorrect ability type: $other")
        }

        gameState
          .modify(_.abilities)
          .using(_.updated(abilityId, ability))
    }

    result
  }
}
