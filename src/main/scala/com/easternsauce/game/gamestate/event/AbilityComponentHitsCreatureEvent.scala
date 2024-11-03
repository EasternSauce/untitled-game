package com.easternsauce.game.gamestate.event
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.softwaremill.quicklens.{ModifyPimp, QuicklensMapAt}

import scala.util.chaining.scalaUtilChainingOps

case class AbilityComponentHitsCreatureEvent(
    creatureId: GameEntityId[Creature],
    abilityComponentId: GameEntityId[AbilityComponent],
    areaId: AreaId
) extends AreaGameStateEvent {

  override def applyToGameState(
      gameState: GameState
  )(implicit game: CoreGame): GameState = {
    if (
      gameState.abilityComponents
        .contains(abilityComponentId) && gameState.creatures.contains(
        creatureId
      )
    ) {
      val abilityComponent = gameState.abilityComponents(abilityComponentId)
      val ability = gameState.abilities(abilityComponent.params.abilityId)
      val creature = gameState.creatures(creatureId)

      if (ability.params.creatureId != creatureId) {
        gameState
          .modify(_.creatures.at(creatureId))
          .using(creature => creature
//              .pipe(
//                DamageDealingUtils.registerLastAttackedByCreature(
//                  ability.params.creatureId
//                )
//              )
//              .pipe(
//                DamageDealingUtils.dealDamageToCreature(ability.params.damage)
//              )
          )
          .modify(_.abilityComponents)
          .usingIf(creature.alive && abilityComponent.destroyedOnContact)(
            _.removed(abilityComponentId)
          )
          .pipe(_.removeAbilityIfCompleted(abilityComponent.abilityId))
      } else {
        gameState
      }
    } else {
      gameState
    }
  }
}
