package com.easternsauce.game.entitycreator

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability._
import com.easternsauce.game.gamestate.id.GameEntityId
import com.softwaremill.quicklens.ModifyPimp

trait AbilityComponentCreator {
  this: GameState =>

  private[entitycreator] def createAbilityComponents(implicit
      game: CoreGame
  ): GameState = {

    val componentsToCreate = game.queues.abilityComponentQueue.drain()

    val result = componentsToCreate.foldLeft(this) {
      case (gameState, abilityComponentToCreate: AbilityComponentToCreate) =>
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
          scenarioStepNo = abilityComponentToCreate.scenarioStepNo,
          expirationTime = abilityComponentToCreate.expirationTime,
          spawnPos = abilityComponentToCreate.pos // NEW
        )

        val abilityComponent: AbilityComponent =
          abilityComponentToCreate.componentType match {
            case AbilityComponentType.ArrowComponent      => ??? // ArrowComponent(params)
            case AbilityComponentType.GhostArrowComponent => ??? // GhostArrowComponent(params)
            case AbilityComponentType.ExplosionComponent  => ??? // ExplosionComponent(params)
            case AbilityComponentType.ReturningArrowComponent =>
              ??? // ReturningArrowComponent(params)
            case other =>
              throw new RuntimeException(s"Incorrect ability component type: $other")
          }

        gameState
          .modify(_.abilityComponents)
          .using(_.updated(abilityComponentId, abilityComponent.init()))
    }

    result
  }
}
