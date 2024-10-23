package com.easternsauce.game.entitycreator

import com.easternsauce.game.gamestate.ability.AbilityType.AbilityType
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

case class AbilityToCreate(
    abilityType: AbilityType,
    currentAreaId: AreaId,
    creatureId: GameEntityId[Creature],
    pos: Vector2f,
    facingVector: Vector2f
)
