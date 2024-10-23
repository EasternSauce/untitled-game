package com.easternsauce.game.entitycreator

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.{Ability, AbilityParams, AbilityType, Arrow}
import com.easternsauce.game.gamestate.id.GameEntityId
import com.softwaremill.quicklens.ModifyPimp

case class AbilityCreator() extends EntityCreator {

  override def createEntities(implicit
      game: CoreGame
  ): GameState => GameState = { gameState =>
    val result = game.queues.abilitiesToCreate.foldLeft(gameState) {
      case (gameState, abilityToCreate) =>
        val abilityId =
          GameEntityId[Ability](
            "ability" + (Math.random() * 1000000).toInt
          )

        val params = AbilityParams(
          id = abilityId,
          currentAreaId = abilityToCreate.currentAreaId,
          creatureId = abilityToCreate.creatureId,
          pos = abilityToCreate.pos,
          facingVector = abilityToCreate.facingVector
        )
        val ability =
          if (abilityToCreate.abilityType == AbilityType.Arrow) {
            Arrow(params)
          } else {
            throw new RuntimeException("incorrect ability component type")
          }

        gameState
          .modify(_.abilities)
          .using(_.updated(abilityId, ability.init()))
    }

    game.queues.abilitiesToCreate.clear()

    result
  }
}
