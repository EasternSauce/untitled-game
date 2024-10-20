package com.easternsauce.game.gamestate.event
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.AbilityType.AbilityType
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

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

    game.gameplay.entityCreators.scheduleAbilityToCreate(
      abilityType,
      areaId,
      creatureId,
      pos,
      facingVector
    )
    gameState
  }
}
