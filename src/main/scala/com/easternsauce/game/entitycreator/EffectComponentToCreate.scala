package com.easternsauce.game.entitycreator

import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId

case class EffectComponentToCreate(
    effectType: EffectComponentType,
    currentAreaId: AreaId,
    targetCreatureId: GameEntityId[Creature],
    duration: Float,
    tickInterval: Option[Float] = None
)
