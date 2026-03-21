package com.easternsauce.game.gamestate.effect

import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId

case class EffectComponentParams(
    targetCreatureId: GameEntityId[Creature],
    duration: Float,
    timeElapsed: Float = 0f,
    tickInterval: Option[Float] = None,
    isScheduledToBeRemoved: Boolean = false
)
