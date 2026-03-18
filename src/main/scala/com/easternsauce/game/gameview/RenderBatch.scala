package com.easternsauce.game.gameview

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.{Rectangle => GdxRectangle}
import com.easternsauce.game.math.GameRectangle
import com.easternsauce.game.math.Vector2f
import space.earlygrey.shapedrawer.ShapeDrawer

case class RenderBatch() {

  private var spriteBatch: SpriteBatch = _
  private var shapeDrawer: ShapeDrawer = _

  private var texture: Texture = _

  def init(): Unit = {
    spriteBatch = new SpriteBatch()
    shapeDrawer = new ShapeDrawer(spriteBatch, createTextureAndRegion())
  }

  private def createTextureAndRegion(): TextureRegion = {
    val pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888)
    pixmap.setColor(Color.WHITE)
    pixmap.drawPixel(0, 0)
    texture = new Texture(pixmap) // remember to dispose of later

    pixmap.dispose()
    new TextureRegion(texture, 0, 0, 1, 1)
  }

  def begin(): Unit = spriteBatch.begin()

  def end(): Unit = spriteBatch.end()

  def draw(region: TextureRegion, x: Float, y: Float): Unit = {
    spriteBatch.draw(region, x, y)
  }

  def draw(
      region: TextureRegion,
      x: Float,
      y: Float,
      width: Int,
      height: Int,
      rotation: Float = 0f
  ): Unit = {
    spriteBatch.draw(
      region,
      x,
      y,
      width / 2f,
      height / 2f,
      width.toFloat,
      height.toFloat,
      1f,
      1f,
      rotation
    )
  }

  def setProjectionMatrix(projection: Matrix4): Unit = {
    spriteBatch.setProjectionMatrix(projection)
  }

  def filledRectangle(rect: GameRectangle, color: Color): Unit = {
    shapeDrawer.filledRectangle(
      new GdxRectangle(rect.x, rect.y, rect.width, rect.height),
      color
    )
  }

  def circle(
      center: Vector2f,
      radius: Float,
      color: Color,
      lineWidth: Float
  ): Unit = {
    shapeDrawer.setColor(color)
    shapeDrawer.circle(center.x, center.y, radius, lineWidth)
  }

  def drawFont(
      font: BitmapFont,
      str: String,
      pos: Vector2f,
      color: Color
  ): Unit = {
    font.setColor(color)
    font.draw(spriteBatch, str, pos.x, pos.y)
  }

  def underlyingSpriteBatch: SpriteBatch = spriteBatch

  def dispose(): Unit = {
    spriteBatch.dispose()
    texture.dispose()
  }

  def getProjectionMatrix: Matrix4 =
    underlyingSpriteBatch.getProjectionMatrix
}
