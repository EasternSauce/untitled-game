package com.easternsauce.game.entitycreator

import com.easternsauce.game.gamestate.creature.CreatureType.CreatureType
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

case class EnemyToCreate(
    spawnPointId: String,
    creatureType: CreatureType,
    areaId: AreaId,
    pos: Vector2f
)
