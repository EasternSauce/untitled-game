package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamestate.id.AreaId

import scala.collection.mutable

case class StaticEnvironmentBodies() {

  private var areaWorlds: mutable.Map[AreaId, AreaWorld] = _
  private var staticBodiesByArea: Map[AreaId, List[PhysicsBody]] = Map()

  def init(
      tiledMaps: mutable.Map[AreaId, GameTiledMap],
      areaWorlds: mutable.Map[AreaId, AreaWorld]
  )(implicit game: CoreGame): Unit = {

    this.areaWorlds = areaWorlds
    val factory = StaticBodyFactory()

    tiledMaps.foreach { case (areaId, map) =>
      val world = areaWorlds(areaId)

      val objectCells = MapCellExtractor.staticObjects(map)

      val objectBodies = factory.createStaticObjectBodies(objectCells, world)

      staticBodiesByArea += (areaId -> objectBodies)
    }
  }

  def bodiesForArea(areaId: AreaId): List[PhysicsBody] =
    staticBodiesByArea.getOrElse(areaId, Nil)
}
