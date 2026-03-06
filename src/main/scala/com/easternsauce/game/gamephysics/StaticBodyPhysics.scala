package com.easternsauce.game.gamephysics

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamemap.{GameMapCell, GameTiledMap}
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f
import scala.collection.mutable

case class StaticBodyPhysics() {

  private var staticBodies: List[PhysicsBody] = _
  private var areaWorlds: mutable.Map[AreaId, AreaWorld] = _

  def init(
      tiledMaps: mutable.Map[AreaId, GameTiledMap],
      areaWorlds: mutable.Map[AreaId, AreaWorld]
  )(implicit game: CoreGame): Unit = {
    areaWorlds.foreach { case (areaId, world) =>
      val gameTiledMap = tiledMaps(areaId)

      val terrainTiles =
        terrainTileCells(gameTiledMap) ++ largeStaticObjectCells(gameTiledMap)

      val staticObjects = staticObjectCells(gameTiledMap)

      staticBodies = createStaticBodies(terrainTiles, staticObjects, world)
    }

    this.areaWorlds = areaWorlds
  }

  private def createStaticBodies(
      terrainTileCells: List[GameMapCell],
      staticObjectCells: List[GameMapCell],
      world: AreaWorld
  )(implicit game: CoreGame): List[PhysicsBody] = {
    createTerrainTileBodies(terrainTileCells, world) ++
      createStaticObjectBodies(staticObjectCells, world)
  }

  private def createStaticObjectBodies(
      staticObjectCells: List[GameMapCell],
      world: AreaWorld
  )(implicit game: CoreGame): List[StaticObjectBody] = {
    staticObjectCells
      .map(_.pos())
      .distinct
      .map(createStaticObjectBody(_, world))
  }

  private def createTerrainTileBodies(
      terrainTileCells: List[GameMapCell],
      world: AreaWorld
  )(implicit game: CoreGame): List[TerrainTileBody] = {
    terrainTileCells
      .map(_.pos())
      .distinct
      .map(createTerrainTileBody(_, world))
  }

  private def createStaticObjectBody(
      pos: Vector2f,
      world: AreaWorld
  )(implicit game: CoreGame) = {
    val objectBody = StaticObjectBody("staticObject_" + pos.x + "_" + pos.y)
    objectBody.init(world, pos)
    objectBody
  }

  private def createTerrainTileBody(
      pos: Vector2f,
      world: AreaWorld
  )(implicit game: CoreGame) = {
    val terrainTileBody = TerrainTileBody("terrainTile_" + pos.x + "_" + pos.y)
    terrainTileBody.init(world, pos)
    terrainTileBody
  }

  private def largeStaticObjectCells(
      gameTiledMap: GameTiledMap
  ): List[GameMapCell] = {
    gameTiledMap.layer("object").cells
  }

  private def staticObjectCells(
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

  private def terrainTileCells(
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
