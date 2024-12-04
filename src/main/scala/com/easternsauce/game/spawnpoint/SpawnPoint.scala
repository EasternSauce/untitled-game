package com.easternsauce.game.spawnpoint

import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

case class SpawnPoint(
    id: String,
    areaId: AreaId,
    pos: Vector2f,
    creaturesToSpawn: List[CreaturesToSpawn]
)
