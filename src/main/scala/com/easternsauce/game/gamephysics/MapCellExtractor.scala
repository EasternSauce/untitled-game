package com.easternsauce.game.gamephysics

import com.easternsauce.game.Constants
import com.easternsauce.game.gamemap.{GameMapCell, GameTiledMap}

object MapCellExtractor {

  // terrain tiles like walls, water, or large objects
  def terrainTiles(map: GameTiledMap): List[GameMapCell] =
    map.layer("collision").cells.filter(cell => {
      val id = cell.tiledCell.getTile.getId
      id == Constants.WallCollisionCellId ||
        id == Constants.LargeObjectCollisionCellId ||
        id == Constants.WaterGroundCollisionCellId
    }) ++
      map.layer("manual_collision").cells.filter(_.tiledCell.getTile.getId == Constants.WaterGroundCollisionCellId)

  // small static objects like crates, barrels
  def smallObjects(map: GameTiledMap): List[GameMapCell] = {
    val smallId = Constants.SmallObjectCollisionCellId
    map.layer("collision").cells.filter(_.tiledCell.getTile.getId == smallId) ++
      map.layer("manual_collision").cells.filter(_.tiledCell.getTile.getId == smallId)
  }

  // large static objects like buildings, rocks
  def largeObjects(map: GameTiledMap): List[GameMapCell] =
    map.layer("object").cells
}