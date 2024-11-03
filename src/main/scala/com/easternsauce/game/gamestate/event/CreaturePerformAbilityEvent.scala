package com.easternsauce.game.gamestate.event
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.entitycreator.AbilityToCreate
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.AbilityType
import com.easternsauce.game.gamestate.ability.AbilityType.AbilityType
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f
import com.softwaremill.quicklens.{ModifyPimp, QuicklensMapAt}

case class CreaturePerformAbilityEvent(
    creatureId: GameEntityId[Creature],
    areaId: AreaId,
    abilityType: AbilityType,
    pos: Vector2f,
    destination: Vector2f
) extends AreaGameStateEvent {

  override def applyToGameState(
      gameState: GameState
  )(implicit game: CoreGame): GameState = {
    val creature = game.gameState.creatures(creatureId)

    if (
      !creature.params
        .abilityCooldownTimers(AbilityType.Arrow)
        .running || creature.params
        .abilityCooldownTimers(AbilityType.Arrow)
        .time > 1f
    ) {
      game.queues.abilitiesToCreate += AbilityToCreate(
        abilityType,
        areaId,
        creatureId,
        pos,
        creature.pos.vectorTowards(destination)
      )
      gameState
        .modify(
          _.creatures
            .at(creatureId)
            .params
            .abilityCooldownTimers
            .at(abilityType)
        )
        .using(_.restart())
        .modify(_.creatures.at(creatureId).params.attackAnimationTimer)
        .using(_.restart())
        .modify(_.creatures.at(creatureId).params.destination)
        .setTo(creature.pos)
        .modify(_.creatures.at(creatureId).params.facingVector)
        .setTo(creature.pos.vectorTowards(destination))
    } else {
      gameState
    }

  }
}
