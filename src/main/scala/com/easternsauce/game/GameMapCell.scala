package com.easternsauce.game

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.easternsauce.game.gamestate.GameState

case class GameMapCell(tiledCell: Cell, areaId: AreaId, pos: Vector2f) extends Renderable {
  override def pos(gameState: GameState): Vector2f = pos

  override def areaId(gameState: GameState): AreaId = areaId

  override def renderPriority(gameState: GameState): Boolean = false

  override def render(batch: GameSpriteBatch, worldCameraPos: Vector2f, gameState: GameState): Unit = {
    val textureRegion = tiledCell.getTile.getTextureRegion
    val textureWidth = textureRegion.getRegionWidth
    val textureHeight = textureRegion.getRegionHeight

    val screenPos =
      IsometricProjection.translatePosIsoToScreen(
        Vector2f(pos.x + 0.75f, pos.y - 0.85f)
      )

//    if (worldCameraPos.distance(screenPos) < 1000f) {
      batch.draw(
        textureRegion,
        screenPos.x + Constants.TileCenterX + tiledCell.getTile.getOffsetX,
        screenPos.y + Constants.TileCenterY + tiledCell.getTile.getOffsetY,
        (textureWidth * Constants.MapTextureScale).toInt,
        (textureHeight * Constants.MapTextureScale).toInt
      )
//    }
  }
}
