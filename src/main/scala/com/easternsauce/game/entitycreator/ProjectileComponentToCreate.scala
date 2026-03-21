package com.easternsauce.game.entitycreator

import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId

case class ProjectileComponentToCreate(
    abilityId: GameEntityId[_],
    componentType: ProjectileComponentType,
    currentAreaId: AreaId,
    creatureId: GameEntityId[Creature],
    pos: (Float, Float),
    velocity: (Float, Float),
    damage: Float,
    expirationTime: Option[Float] = None
)
