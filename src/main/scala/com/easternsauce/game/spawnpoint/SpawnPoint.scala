package com.easternsauce.game.spawnpoint

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.entitycreator.EnemyToCreate
import com.easternsauce.game.gamestate.id.AreaId

case class SpawnPoint(
    spawnPointData: SpawnPointData
) {
  def update()(implicit game: CoreGame): SpawnPoint = {
    spawnPointData.creaturesToSpawn.foreach(creaturesToSpawn => {
      val creaturesAlreadySpawnedCount = game.gameState.creatures.values
        .filter(_.params.spawnPointId.contains(spawnPointData.spawnPointId))
        .filter(_.params.creatureType == creaturesToSpawn.creatureType)
        .count(_.alive)

      for (
        _ <-
          0 until creaturesToSpawn.numOfCreatures - creaturesAlreadySpawnedCount
      ) {
        game.queues.creaturesToCreate += EnemyToCreate(
          spawnPointId = spawnPointData.spawnPointId,
          creatureType = creaturesToSpawn.creatureType,
          areaId = spawnPointData.spawnPointAreaId,
          pos = spawnPointData.spawnPointPos
        )
      }
    })

    this
  }

  def areaId: AreaId = spawnPointData.spawnPointAreaId
}
