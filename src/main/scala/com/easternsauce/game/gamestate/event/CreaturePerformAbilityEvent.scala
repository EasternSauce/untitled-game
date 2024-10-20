package com.easternsauce.game.gamestate.event
import com.easternsauce.game.core.CoreGame
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
    facingVector: Vector2f
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
    ) {}
    game.gameplay.entityCreators.scheduleAbilityToCreate(
      abilityType,
      areaId,
      creatureId,
      pos,
      facingVector
    )
    gameState
      .modify(
        _.creatures.at(creatureId).params.abilityCooldownTimers.at(abilityType)
      )
      .using(_.restart())
  }
}
