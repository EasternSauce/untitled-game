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
    val result = game.queues.abilitiesToCreate.foldLeft(this) {
      case (gameState, abilityToCreate) =>
        val abilityId =
          GameEntityId[Ability](
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
        val ability =
          if (abilityToCreate.abilityType == AbilityType.Arrow) {
            Arrow(params)
          } else if (abilityToCreate.abilityType == AbilityType.MeleeAttack) {
            MeleeAttack(params)
          } else {
            throw new RuntimeException("incorrect ability component type")
          }

        gameState
          .modify(_.abilities)
          .using(_.updated(abilityId, ability))
    }

    game.queues.abilitiesToCreate.clear()

    result
  }
}
