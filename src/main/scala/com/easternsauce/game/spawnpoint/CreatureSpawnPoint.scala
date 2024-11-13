package com.easternsauce.game.spawnpoint

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.entitycreator.EnemyToCreate
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

case class CreatureSpawnPoint(
spawnPointData: SpawnPointData
) {
  def update()(implicit game: CoreGame): Unit = {
    spawnPointData.creaturesToSpawn.foreach(creaturesToSpawn => {
      val creaturesAlreadySpawnedCount = game.gameState.creatures.values
        .filter(_.params.creatureType == creaturesToSpawn.creatureType)
        .filter(_.alive)
        .count(_.params.spawnPointId.contains(spawnPointData.spawnPointId))

      println("num: " + creaturesToSpawn.numOfCreatures)
      println("stuff: " + creaturesAlreadySpawnedCount)

      for (_ <- 0 until creaturesToSpawn.numOfCreatures - creaturesAlreadySpawnedCount) {
        game.queues.creaturesToCreate += EnemyToCreate(
          spawnPointId = spawnPointData.spawnPointId,
          creatureType = creaturesToSpawn.creatureType,
          areaId = spawnPointData.spawnPointAreaId,
          pos = spawnPointData.spawnPointPos
        )
      }
    })

  }
}
