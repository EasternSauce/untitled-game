package com.easternsauce.game.core

import com.easternsauce.game.Constants
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.spawnpoint.CreatureSpawnPoint

case class SpawnPointsManager() {

  private var spawnPoints: List[CreatureSpawnPoint] = _

  def init(): Unit = {
    spawnPoints = Constants.MapAreaSpawnPoints.map(CreatureSpawnPoint)
  }

  def updateForArea(areaId: AreaId)(implicit game: CoreGame): Unit = {
    spawnPoints.filter(_.spawnPointData.spawnPointAreaId == areaId).foreach(_.update())
  }
}
