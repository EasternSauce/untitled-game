package com.easternsauce.game.gameview

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.easternsauce.game.Constants
import com.easternsauce.game.math.Vector2f

case class FpsCountRenderer() {
  def init(): Unit = {}

  def render(spriteBatchHolder: SpriteBatchHolder, skin: Skin): Unit = {
    val fps = Gdx.graphics.getFramesPerSecond
    val font = skin.getFont("default-font")
    spriteBatchHolder.hudSpriteBatch.drawFont(
      font,
      fps + " fps",
      Vector2f(Constants.WindowWidth / 2f - 50f, Constants.WindowHeight / 2f),
      Color.ORANGE
    )
  }
}
