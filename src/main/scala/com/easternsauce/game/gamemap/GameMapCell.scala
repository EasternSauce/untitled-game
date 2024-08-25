package com.easternsauce.game.gamemap

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.easternsauce.game._
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gameview.{GameSpriteBatch, Renderable}
import com.easternsauce.game.math.{IsometricProjection, Vector2f}

case class GameMapCell(tiledCell: Cell, areaId: AreaId, pos: Vector2f)
    extends Renderable {
  override def pos()(implicit game: CoreGame): Vector2f = pos

  override def areaId(gameState: GameState): AreaId = areaId

  override def renderPriority(gameState: GameState): Boolean = false

  override def render(batch: GameSpriteBatch, worldCameraPos: Vector2f)(implicit
      game: CoreGame
  ): Unit = {
    val textureRegion = tiledCell.getTile.getTextureRegion
    val textureWidth = textureRegion.getRegionWidth
    val textureHeight = textureRegion.getRegionHeight

    val screenPos =
      IsometricProjection.translatePosIsoToScreen(
        Vector2f(pos.x + Constants.RenderShiftX, pos.y + Constants.RenderShiftY)
      )

    if (worldCameraPos.distance(screenPos) < Constants.RenderDistance) {
      batch.draw(
        textureRegion,
        screenPos.x + Constants.TileCenterX + tiledCell.getTile.getOffsetX,
        screenPos.y + Constants.TileCenterY + tiledCell.getTile.getOffsetY,
        (textureWidth * Constants.MapTextureScale).toInt,
        (textureHeight * Constants.MapTextureScale).toInt
      )
    }
  }
}
