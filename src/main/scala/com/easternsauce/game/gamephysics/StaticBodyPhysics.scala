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

    staticBodies = List()

    areaWorlds.foreach { case (areaId, world) =>
      val GameTiledMap = tiledMaps(areaId)

      val terrainCollisions =
        getTerrainCollisionCells(GameTiledMap) ++ getBigObjectCells(
          GameTiledMap
        )

      val objectCollisions = getObjectCollisionCells(GameTiledMap)

      staticBodies ++= createStaticBodies(
        terrainCollisions,
        objectCollisions,
        world,
        gameState
      )
    }

    this.areaWorlds = areaWorlds
  }

  private def createStaticBodies(
      terrainCollisions: List[GameMapCell],
      objectCollisions: List[GameMapCell],
      world: AreaWorld,
      gameState: GameState
  ): List[PhysicsBody] = {
    createTerrainBodies(terrainCollisions, world, gameState) ++
      createObjectBodies(objectCollisions, world, gameState)
  }

  private def createObjectBodies(
      objectCollisions: List[GameMapCell],
      world: AreaWorld,
      gameState: GameState
  ): List[ObjectBody] = {
    objectCollisions
      .map(_.pos(gameState))
      .distinct
      .map(createObjectBody(_, world, gameState))
  }

  private def createTerrainBodies(
      terrainCollisions: List[GameMapCell],
      world: AreaWorld,
      gameState: GameState
  ): List[TerrainBody] = {
    terrainCollisions
      .map(_.pos(gameState))
      .distinct
      .map(createTerrainBody(_, world, gameState))
  }

  private def createObjectBody(
      pos: Vector2f,
      world: AreaWorld,
      gameState: GameState
  ) = {
    val objectBody = ObjectBody("objectBody_" + pos.x + "_" + pos.y)
    objectBody.init(world, pos, gameState)
    objectBody
  }

  private def createTerrainBody(
      pos: Vector2f,
      world: AreaWorld,
      gameState: GameState
  ) = {
    val terrainBody = TerrainBody("terrainBody_" + pos.x + "_" + pos.y)
    terrainBody.init(world, pos, gameState)
    terrainBody
  }

  private def getBigObjectCells(
      gameTiledMap: GameTiledMap
  ): List[GameMapCell] = {
    gameTiledMap.layer("object").cells
  }

  private def getObjectCollisionCells(gameTiledMap: GameTiledMap) = {
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

  private def getTerrainCollisionCells(
      GameTiledMap: GameTiledMap
  ): List[GameMapCell] = {
    GameTiledMap
      .layer("collision")
      .cells
      .filter(GameMapCell => {
        val cellId = GameMapCell.tiledCell.getTile.getId
        cellId == Constants.WaterGroundCollisionCellId ||
        cellId == Constants.BigObjectCollisionCellId ||
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
