package com.easternsauce.game.gamestate.event

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.{Ability, AbilityState}
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f
import com.softwaremill.quicklens.{ModifyPimp, QuicklensMapAt}

case class CreatureGoToEvent(
    creatureId: GameEntityId[Creature],
    areaId: AreaId,
    destination: Vector2f
) extends AreaGameStateEvent {

  override def applyToGameState(
      gameState: GameState
  )(implicit game: CoreGame): GameState = {
    if (
      gameState.creatures
        .contains(creatureId) && gameState.creatures(creatureId).isAlive
    ) {
      val creature = gameState.creatures(creatureId)

      val vectorTowardsDestination =
        creature.pos.vectorTowards(creature.params.destination)

      def isAbilityMovementBlocking(ability: Ability): Boolean =
        ability.params.creatureId == creatureId &&
          ability.currentState == AbilityState.Channelling &&
          ability.currentStateTime > ability.channelTime * 0.8f

      val movementBlockingAbility =
        gameState.abilities.values.find(isAbilityMovementBlocking)

      if (movementBlockingAbility.isEmpty) {
        gameState
          .modify(_.creatures.at(creatureId))
          .using(
            _.modify(_.params.destination)
              .setTo(destination)
              .modify(_.params.facingVector)
              .setToIf(vectorTowardsDestination.length > 0)(
                vectorTowardsDestination
              )
              .modify(_.params.isDestinationReached)
              .setTo(false)
          )
          .modify(_.abilities.each)
          .using(ability => {
            if (
              ability.params.creatureId == creatureId && ability.currentState == AbilityState.Channelling
            ) {
              ability
                .modify(_.params.state)
                .setTo(AbilityState.Cancelled)
                .modify(_.params.stateTimer)
                .using(_.restart())
            } else {
              ability
            }
          })
      } else {
        gameState
      }

    } else {
      gameState
    }
  }
}
