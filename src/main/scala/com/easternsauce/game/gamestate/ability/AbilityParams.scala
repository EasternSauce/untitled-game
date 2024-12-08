package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.gamestate.SimpleTimer
import com.easternsauce.game.gamestate.ability.AbilityState.AbilityState
import com.easternsauce.game.gamestate.ability.AbilityType.AbilityType
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

case class AbilityParams(
    id: GameEntityId[Ability],
    currentAreaId: AreaId,
    creatureId: GameEntityId[Creature],
    abilityType: AbilityType,
    state: AbilityState,
    pos: Vector2f,
    facingVector: Vector2f = Vector2f(0, 1),
    damage: Float = 10f,
    stateTimer: SimpleTimer = SimpleTimer(isRunning = true),
    velocity: Vector2f = Vector2f(0, 1),
    speed: Float = 10f,
    targetId: Option[GameEntityId[Creature]] = None
) {}
