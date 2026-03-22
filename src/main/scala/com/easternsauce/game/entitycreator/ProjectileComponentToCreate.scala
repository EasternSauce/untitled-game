package com.easternsauce.game.entitycreator

import com.easternsauce.game.gamestate.ability.Ability
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.gamestate.projectile.ProjectileComponentType.ProjectileComponentType
import com.easternsauce.game.math.Vector2f

case class ProjectileComponentToCreate(
    abilityId: GameEntityId[Ability],
    componentType: ProjectileComponentType,
    currentAreaId: AreaId,
    creatureId: GameEntityId[Creature],
    pos: Vector2f,
    facingVector: Vector2f,
    damage: Float,
    scenarioStepNo: Int,
    expirationTime: Option[Float]
)
