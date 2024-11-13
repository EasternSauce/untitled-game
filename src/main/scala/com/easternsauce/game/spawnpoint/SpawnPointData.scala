package com.easternsauce.game.spawnpoint

import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

case class SpawnPointData(
    spawnPointId: String,
    spawnPointAreaId: AreaId,
    spawnPointPos: Vector2f,
    creaturesToSpawn: List[CreaturesToSpawn]
)
