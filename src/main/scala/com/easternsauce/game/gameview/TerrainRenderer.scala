package com.easternsauce.game.gameview

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

case class TerrainRenderer() {

  def renderBottomTerrainLayer(
      areaId: AreaId,
      tiledMap: GameTiledMap,
      batch: RenderBatch,
      worldCameraPos: Vector2f
  )(implicit game: CoreGame): Unit = {

    tiledMap.renderBottomLayers(batch, worldCameraPos)
  }

  def renderTopTerrainLayer(
      areaId: AreaId,
      tiledMap: GameTiledMap,
      batch: RenderBatch,
      worldCameraPos: Vector2f
  )(implicit game: CoreGame): Unit = {

    tiledMap.renderTopLayers(batch, worldCameraPos)
  }
}
