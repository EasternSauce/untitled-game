package com.easternsauce.game.entitycreator

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability._
import com.easternsauce.game.gamestate.effect.EffectComponent
import com.easternsauce.game.gamestate.effect.EffectComponentType
import com.easternsauce.game.gamestate.id.GameEntityId
import com.softwaremill.quicklens.ModifyPimp

trait EffectComponentCreator {
  this: GameState =>

  private[entitycreator] def createEffectComponents(implicit
      game: CoreGame
  ): GameState = {

    val componentsToCreate = game.queues.effectComponentQueue.drain()

    val result = componentsToCreate.foldLeft(this) {
      case (gameState, effectComponentToCreate: EffectComponentToCreate) =>
        val effectComponentId =
          GameEntityId[EffectComponent](
            "effect" + (Math.random() * 1000000).toInt
          )

        val ability = gameState.abilities(effectComponentToCreate.abilityId)

        val params = EffectComponentParams(
          id = effectComponentId,
          abilityId = effectComponentToCreate.abilityId,
          currentAreaId = effectComponentToCreate.currentAreaId,
          creatureId = effectComponentToCreate.creatureId,
          pos = effectComponentToCreate.pos,
          facingVector = effectComponentToCreate.facingVector,
          damage = effectComponentToCreate.damage,
          scenarioStepNo = effectComponentToCreate.scenarioStepNo,
          expirationTime = effectComponentToCreate.expirationTime,
          spawnPos = effectComponentToCreate.pos // NEW
        )

        val effectComponent: EffectComponent =
          effectComponentToCreate.componentType match {
            case EffectComponentType.BuffComponent => ???
            case other =>
              throw new RuntimeException(s"Incorrect effect component type: $other")
          }

        gameState
          .modify(_.effectComponents)
          .using(_.updated(effectComponentId, effectComponent.init()))
    }

    result
  }
}
