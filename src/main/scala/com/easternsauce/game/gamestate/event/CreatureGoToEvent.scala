package com.easternsauce.game.gamestate.event

import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f
import com.softwaremill.quicklens.{ModifyPimp, QuicklensMapAt}

class CreatureGoToEvent(
    creatureId: GameEntityId[Creature],
    destination: Vector2f
) extends GameStateEvent {

  override def applyToGameState(gameState: GameState): GameState = {
    if (
      gameState.creatures
        .contains(creatureId) && gameState.creatures(creatureId).alive
    ) {
      val creature = gameState.creatures(creatureId)

      val vectorTowardsDestination =
        creature.pos.vectorTowards(creature.params.destination)

      gameState
        .modify(_.creatures.at(creatureId))
        .using(
          _.modify(_.params.destination)
            .setTo(destination)
        )
        .modify(_.creatures.at(creatureId).params.facingVector)
        .setToIf(vectorTowardsDestination.length > 0)(vectorTowardsDestination)
    } else {
      gameState
    }
  }
}