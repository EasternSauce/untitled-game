package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.gamestate.SimpleTimer
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

case class AbilityParams(
    id: GameEntityId[Ability],
    currentAreaId: AreaId,
    creatureId: GameEntityId[Creature],
    pos: Vector2f,
    facingVector: Vector2f = Vector2f(1, 0),
    damage: Float,
    animationTimer: SimpleTimer = SimpleTimer(running = true),
    velocity: Vector2f = Vector2f(0, 0)
) {}
