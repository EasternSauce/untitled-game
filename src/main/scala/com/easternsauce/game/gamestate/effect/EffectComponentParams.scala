package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.gamestate.SimpleTimer
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.effect.EffectComponent
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

case class EffectComponentParams(
    id: GameEntityId[EffectComponent],
    abilityId: GameEntityId[Ability],
    currentAreaId: AreaId,
    creatureId: GameEntityId[Creature],
    pos: Vector2f,
    facingVector: Vector2f = Vector2f(0, 1),
    damage: Float,
    generalTimer: SimpleTimer = SimpleTimer(isRunning = true),
    scenarioStepNo: Int,
    isScheduledToBeRemoved: Boolean = false,
    isContinueScenario: Boolean = true,
    expirationTime: Option[Float],
    spawnPos: Vector2f,
    hasLeftInitialSurface: Boolean = false
)
