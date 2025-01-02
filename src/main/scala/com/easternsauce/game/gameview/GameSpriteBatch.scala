package com.easternsauce.game.gameview

import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch, TextureRegion}
import com.badlogic.gdx.graphics.{Color, Pixmap, Texture}
import com.badlogic.gdx.math.{Matrix4, Rectangle => GdxRectangle}
import com.easternsauce.game.math.{GameRectangle, Vector2f}
import space.earlygrey.shapedrawer.ShapeDrawer

case class GameSpriteBatch() {

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
    texture = new Texture(pixmap) //remember to dispose of later

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

}
