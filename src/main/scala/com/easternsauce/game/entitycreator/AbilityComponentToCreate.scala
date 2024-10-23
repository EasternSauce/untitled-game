package com.easternsauce.game.entitycreator

import com.easternsauce.game.gamestate.ability.Ability
import com.easternsauce.game.gamestate.ability.AbilityComponentType.AbilityComponentType
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

case class AbilityComponentToCreate(
    abilityId: GameEntityId[Ability],
    componentType: AbilityComponentType,
    currentAreaId: AreaId,
    creatureId: GameEntityId[Creature],
    pos: Vector2f,
    facingVector: Vector2f,
    damage: Float
)
