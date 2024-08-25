package com.easternsauce.game.gamephysics

import com.easternsauce.game.gamemap.{GameMapCell, GameTiledMap}
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f
import com.easternsauce.game.{Constants, CoreGame}

import scala.collection.mutable

case class StaticBodyPhysics() {

  private var staticBodies: List[PhysicsBody] = _
  private var areaWorlds: mutable.Map[AreaId, AreaWorld] = _

  def init(
      tiledMaps: mutable.Map[AreaId, GameTiledMap],
      areaWorlds: mutable.Map[AreaId, AreaWorld]
  )(implicit game: CoreGame): Unit = {
    areaWorlds.foreach { case (areaId, world) =>
      val GameTiledMap = tiledMaps(areaId)

      val mapTerrainCollisions =
        mapTerrainCollisionCells(GameTiledMap) ++ largeMapObjectCells(
          GameTiledMap
        )

      val mapObjectCollisions = mapObjectCollisionCells(GameTiledMap)

      staticBodies =
        createStaticBodies(mapTerrainCollisions, mapObjectCollisions, world)
    }

    this.areaWorlds = areaWorlds
  }

  private def createStaticBodies(
      mapTerrainCollisions: List[GameMapCell],
      mapObjectCollisions: List[GameMapCell],
      world: AreaWorld
  )(implicit game: CoreGame): List[PhysicsBody] = {
    createMapTerrainBodies(mapTerrainCollisions, world) ++
      createMapObjectBodies(mapObjectCollisions, world)
  }

  private def createMapObjectBodies(
      objectCollisions: List[GameMapCell],
      world: AreaWorld
  )(implicit game: CoreGame): List[MapObjectBody] = {
    objectCollisions
      .map(_.pos())
      .distinct
      .map(createMapObjectBody(_, world))
  }

  private def createMapTerrainBodies(
      terrainCollisions: List[GameMapCell],
      world: AreaWorld
  )(implicit game: CoreGame): List[MapTerrainBody] = {
    terrainCollisions
      .map(_.pos())
      .distinct
      .map(createMapTerrainBody(_, world))
  }

  private def createMapObjectBody(
      pos: Vector2f,
      world: AreaWorld
  )(implicit game: CoreGame) = {
    val objectBody = MapObjectBody("objectBody_" + pos.x + "_" + pos.y)
    objectBody.init(world, pos)
    objectBody
  }

  private def createMapTerrainBody(
      pos: Vector2f,
      world: AreaWorld
  )(implicit game: CoreGame) = {
    val mapTerrainBody = MapTerrainBody("terrainBody_" + pos.x + "_" + pos.y)
    mapTerrainBody.init(world, pos)
    mapTerrainBody
  }

  private def largeMapObjectCells(
      gameTiledMap: GameTiledMap
  ): List[GameMapCell] = {
    gameTiledMap.layer("object").cells
  }

  private def mapObjectCollisionCells(
      gameTiledMap: GameTiledMap
  ): List[GameMapCell] = {
    gameTiledMap
      .layer("collision")
      .cells
      .filter(
        _.tiledCell.getTile.getId == Constants.SmallObjectCollisionCellId
      ) ++
      gameTiledMap
        .layer("manual_collision")
        .cells
        .filter(
          _.tiledCell.getTile.getId == Constants.SmallObjectCollisionCellId
        )
  }

  private def mapTerrainCollisionCells(
      GameTiledMap: GameTiledMap
  ): List[GameMapCell] = {
    GameTiledMap
      .layer("collision")
      .cells
      .filter(GameMapCell => {
        val cellId = GameMapCell.tiledCell.getTile.getId
        cellId == Constants.WaterGroundCollisionCellId ||
        cellId == Constants.LargeObjectCollisionCellId ||
        cellId == Constants.WallCollisionCellId
      }) ++
      GameTiledMap
        .layer("manual_collision")
        .cells
        .filter(
          _.tiledCell.getTile.getId == Constants.WaterGroundCollisionCellId
        )
  }
}
