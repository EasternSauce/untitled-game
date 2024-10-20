package com.easternsauce.game.gamestate.event

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
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
            .modify(_.params.facingVector)
            .setToIf(vectorTowardsDestination.length > 0)(
              vectorTowardsDestination
            )
            .modify(_.params.destinationReached)
            .setTo(false)
        )
    } else {
      gameState
    }
  }
}
