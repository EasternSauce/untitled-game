package com.easternsauce.game.gamephysics

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamemap.GameMapCell
import com.easternsauce.game.math.Vector2f

case class StaticBodyFactory() {

  private def createTerrainTileBody(pos: Vector2f, world: AreaWorld)(implicit game: CoreGame): TerrainTileBody = {
    val body = TerrainTileBody(s"terrainTile_${pos.x}_${pos.y}")
    body.init(world, pos)
    body
  }

  private def createSmallObjectBody(pos: Vector2f, world: AreaWorld)(implicit game: CoreGame): StaticObjectBody = {
    val body = StaticObjectBody(s"smallObject_${pos.x}_${pos.y}")
    body.init(world, pos)
    body
  }

  def createTerrainTileBodies(cells: List[GameMapCell], world: AreaWorld)(implicit game: CoreGame): List[TerrainTileBody] =
    cells.map(_.pos()).distinct.map(createTerrainTileBody(_, world))

  def createSmallObjectBodies(cells: List[GameMapCell], world: AreaWorld)(implicit game: CoreGame): List[StaticObjectBody] =
    cells.map(_.pos()).distinct.map(createSmallObjectBody(_, world))
}