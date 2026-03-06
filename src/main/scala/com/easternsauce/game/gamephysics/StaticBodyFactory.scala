package com.easternsauce.game.gamephysics

import com.easternsauce.game.gamemap.GameMapCell
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.math.Vector2f

case class StaticBodyFactory() {

  /** Create TerrainTileBody for each cell */
  def createTerrainTileBodies(cells: List[GameMapCell], world: AreaWorld)(implicit game: CoreGame): List[TerrainTileBody] = {
    cells.map(_.pos()).distinct.map { pos =>
      val body = TerrainTileBody(s"terrainTile_${pos.x}_${pos.y}")
      body.init(world, pos)
      body
    }
  }

  /** Create StaticObjectBody for each cell */
  def createStaticObjectBodies(cells: List[GameMapCell], world: AreaWorld)(implicit game: CoreGame): List[StaticObjectBody] = {
    cells.map(_.pos()).distinct.map { pos =>
      val body = StaticObjectBody(s"staticObject_${pos.x}_${pos.y}")
      body.init(world, pos)
      body
    }
  }
}