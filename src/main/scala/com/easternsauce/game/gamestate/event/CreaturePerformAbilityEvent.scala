package com.easternsauce.game.gamestate.event
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.entitycreator.AbilityToCreate
import com.easternsauce.game.gamestate.GameState
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
    destination: Vector2f,
    targetId: Option[GameEntityId[Creature]]
) extends AreaGameStateEvent {

  override def applyToGameState(
      gameState: GameState
  )(implicit game: CoreGame): GameState = {
    val creature = game.gameState.creatures(creatureId)

    gameState.transformIf(
      creature.isAlive && (!creature.params
        .abilityCooldownTimers(abilityType)
        .isRunning ||
        creature.params
          .abilityCooldownTimers(abilityType)
          .time > abilityType.cooldown)
    ) {
      game.queues.abilitiesToCreate += AbilityToCreate(
        abilityType,
        areaId,
        creatureId,
        pos,
        creature.pos.vectorTowards(destination),
        targetId
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
    }

  }
}
