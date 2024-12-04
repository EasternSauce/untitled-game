package com.easternsauce.game.spawnpoint

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.entitycreator.EnemyToCreate
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.id.AreaId

trait SpawnPointUpdater {
  this: GameState =>
  def updateSpawnPoints(areaId: AreaId)(implicit game: CoreGame): GameState = {
    spawnPoints.values.foreach(spawnPoint =>
      if (spawnPoint.areaId == areaId) {
        spawnPoint.creaturesToSpawn.foreach(creaturesToSpawn => {

          val creaturesAlreadySpawnedCount = creatures.values
            .filter(_.params.spawnPointId.contains(spawnPoint.id))
            .filter(_.params.creatureType == creaturesToSpawn.creatureType)
            .count(_.isAlive)

          for (
            _ <-
              0 until creaturesToSpawn.numOfCreatures - creaturesAlreadySpawnedCount
          ) {
            game.queues.enemiesToCreate += EnemyToCreate(
              spawnPointId = spawnPoint.id,
              creatureType = creaturesToSpawn.creatureType,
              areaId = spawnPoint.areaId,
              pos = spawnPoint.pos
            )
          }
        })
      }
    )

    this
  }
}
