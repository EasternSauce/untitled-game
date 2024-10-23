package com.easternsauce.game.entitycreator

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability._
import com.easternsauce.game.gamestate.id.GameEntityId
import com.softwaremill.quicklens.ModifyPimp

case class AbilityComponentCreator() extends EntityCreator {

  override def createEntities(implicit
      game: CoreGame
  ): GameState => GameState = { gameState =>
    val result = game.queues.abilityComponentsToCreate.foldLeft(gameState) {
      case (gameState, abilityComponentToCreate) =>
        val abilityComponentId =
          GameEntityId[AbilityComponent](
            "component" + (Math.random() * 1000000).toInt
          )

        val ability = gameState.abilities(abilityComponentToCreate.abilityId)

        val params = AbilityComponentParams(
          id = abilityComponentId,
          abilityId = abilityComponentToCreate.abilityId,
          currentAreaId = abilityComponentToCreate.currentAreaId,
          creatureId = abilityComponentToCreate.creatureId,
          pos = abilityComponentToCreate.pos,
          facingVector = abilityComponentToCreate.facingVector,
          damage = abilityComponentToCreate.damage,
          velocity = abilityComponentToCreate.facingVector.normalized.multiply(
            ability.params.speed
          )
        )
        val abilityComponent =
          if (
            abilityComponentToCreate.componentType == AbilityComponentType.ArrowComponent
          ) { ArrowComponent(params) }
          else {
            throw new RuntimeException("incorrect ability component type")
          }

        gameState
          .modify(_.abilityComponents)
          .using(_.updated(abilityComponentId, abilityComponent.init()))
    }

    game.queues.abilityComponentsToCreate.clear()

    result
  }

}
