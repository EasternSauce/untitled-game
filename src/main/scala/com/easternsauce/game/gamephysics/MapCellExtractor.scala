package com.easternsauce.game.gamephysics

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamemap.{GameMapCell, GameTiledMap}

object MapCellExtractor {

  /** Terrain tiles: walls, water, or large objects that act as immovable terrain. These positions
    * will be excluded from static objects to prevent overlap.
    */
  def terrainTiles(map: GameTiledMap): List[GameMapCell] = {
    val collisionCells = map
      .layer("collision")
      .cells
      .filter(cell => {
        val id = cell.tiledCell.getTile.getId
        id == Constants.WallCollisionCellId ||
        id == Constants.LargeObjectCollisionCellId ||
        id == Constants.WaterGroundCollisionCellId
      })

    val manualCells = map
      .layer("manual_collision")
      .cells
      .filter(_.tiledCell.getTile.getId == Constants.WaterGroundCollisionCellId)

    collisionCells ++ manualCells
  }

  /** Static objects: all small/large objects placed on the map. Any positions that overlap terrain
    * tiles are filtered out to prevent duplicate bodies.
    */
  def staticObjects(map: GameTiledMap)(implicit game: CoreGame): List[GameMapCell] = {
    val terrainPositions = terrainTiles(map).map(_.pos()).toSet

    // Small objects from collision/manual_collision layers
    val smallId = Constants.SmallObjectCollisionCellId
    val smallObjects = map.layer("collision").cells.filter(_.tiledCell.getTile.getId == smallId) ++
      map.layer("manual_collision").cells.filter(_.tiledCell.getTile.getId == smallId)

    // Large objects from object layer
    val largeObjects = map.layer("object").cells

    // Remove any cells whose positions are already used by terrain tiles
    (smallObjects ++ largeObjects).filter(cell => !terrainPositions.contains(cell.pos()))
  }

}
