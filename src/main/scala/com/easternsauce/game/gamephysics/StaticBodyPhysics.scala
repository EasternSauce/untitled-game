package com.easternsauce.game.gamephysics

import com.easternsauce.game.Constants
import com.easternsauce.game.gamemap.{GameMapCell, GameTiledMap}
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

case class StaticBodyPhysics() {

  private var staticBodies: List[PhysicsBody] = _
  private var areaWorlds: Map[AreaId, AreaWorld] = _

  def init(
      tiledMaps: Map[AreaId, GameTiledMap],
      areaWorlds: Map[AreaId, AreaWorld],
      gameState: GameState
  ): Unit = {
    areaWorlds.foreach { case (areaId, world) =>
      val GameTiledMap = tiledMaps(areaId)

      val mapTerrainCollisions =
        mapTerrainCollisionCells(GameTiledMap) ++ largeMapObjectCells(
          GameTiledMap
        )

      val mapObjectCollisions = mapObjectCollisionCells(GameTiledMap)

      staticBodies = createStaticBodies(
        mapTerrainCollisions,
        mapObjectCollisions,
        world,
        gameState
      )
    }

    this.areaWorlds = areaWorlds
  }

  private def createStaticBodies(
      mapTerrainCollisions: List[GameMapCell],
      mapObjectCollisions: List[GameMapCell],
      world: AreaWorld,
      gameState: GameState
  ): List[PhysicsBody] = {
    createMapTerrainBodies(mapTerrainCollisions, world, gameState) ++
      createMapObjectBodies(mapObjectCollisions, world, gameState)
  }

  private def createMapObjectBodies(
      objectCollisions: List[GameMapCell],
      world: AreaWorld,
      gameState: GameState
  ): List[MapObjectBody] = {
    objectCollisions
      .map(_.pos(gameState))
      .distinct
      .map(createMapObjectBody(_, world, gameState))
  }

  private def createMapTerrainBodies(
      terrainCollisions: List[GameMapCell],
      world: AreaWorld,
      gameState: GameState
  ): List[MapTerrainBody] = {
    terrainCollisions
      .map(_.pos(gameState))
      .distinct
      .map(createMapTerrainBody(_, world, gameState))
  }

  private def createMapObjectBody(
      pos: Vector2f,
      world: AreaWorld,
      gameState: GameState
  ) = {
    val objectBody = MapObjectBody("objectBody_" + pos.x + "_" + pos.y)
    objectBody.init(world, pos, gameState)
    objectBody
  }

  private def createMapTerrainBody(
      pos: Vector2f,
      world: AreaWorld,
      gameState: GameState
  ) = {
    val mapTerrainBody = MapTerrainBody("terrainBody_" + pos.x + "_" + pos.y)
    mapTerrainBody.init(world, pos, gameState)
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
