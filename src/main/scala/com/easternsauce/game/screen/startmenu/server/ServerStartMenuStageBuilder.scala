package com.easternsauce.game.screen.startmenu.server

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, TextButton}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.{InputEvent, Stage}
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.easternsauce.game.CoreGame

//noinspection SameParameterValue
case class ServerStartMenuStageBuilder() {
  def build()(implicit game: CoreGame): Stage = {
    val stage = new Stage(new ScreenViewport())

    val startButton = createButton(
      x = Gdx.graphics.getWidth / 2 - 100,
      y = 400,
      width = 200,
      height = 50,
      text = "Start server",
      listener = new ClickListener() {
        override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
          game.startServer()
        }
      },
      skin = game.view.skin
    )

    val exitButton = createButton(
      x = Gdx.graphics.getWidth / 2 - 100,
      y = 320,
      width = 200,
      height = 50,
      text = "Exit",
      listener = new ClickListener() {
        override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
          game.close()
        }
      },
      skin = game.view.skin
    )

    stage.addActor(startButton)
    stage.addActor(exitButton)

    stage
  }

  private def createButton(
      x: Int,
      y: Int,
      width: Int,
      height: Int,
      text: String,
      listener: ClickListener,
      skin: Skin
  ): TextButton = {
    val button: TextButton =
      new TextButton(text, skin, "default")
    button.setX(x.toFloat)
    button.setY(y.toFloat)
    button.setWidth(width.toFloat)
    button.setHeight(height.toFloat)
    button.addListener(listener)

    button
  }
}
