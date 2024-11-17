package com.easternsauce.game.spawnpoint

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.entitycreator.EnemyToCreate
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.id.AreaId

case class SpawnPoint(
    spawnPointData: SpawnPointData
) {
  def update()(implicit game: CoreGame): GameState => GameState = { gameState =>
    spawnPointData.creaturesToSpawn.foreach(creaturesToSpawn => {
      val creaturesAlreadySpawnedCount = gameState.creatures.values
        .filter(_.params.spawnPointId.contains(spawnPointData.spawnPointId))
        .filter(_.params.creatureType == creaturesToSpawn.creatureType)
        .count(_.alive)

      for (
        _ <-
          0 until creaturesToSpawn.numOfCreatures - creaturesAlreadySpawnedCount
      ) {
        game.queues.enemiesToCreate += EnemyToCreate(
          spawnPointId = spawnPointData.spawnPointId,
          creatureType = creaturesToSpawn.creatureType,
          areaId = spawnPointData.spawnPointAreaId,
          pos = spawnPointData.spawnPointPos
        )
      }
    })

    gameState
  }

  def areaId: AreaId = spawnPointData.spawnPointAreaId
}
