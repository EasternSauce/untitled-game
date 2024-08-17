package com.easternsauce.game.client.screen.pausemenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, TextButton}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.{InputEvent, Stage}
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.easternsauce.game.CoreGame

case class ClientPauseMenuStageBuilder() {
  def build(game: CoreGame): Stage = {
    val stage = new Stage(new ScreenViewport())

    val resumeButton = createButton(
      x = Gdx.graphics.getWidth / 2 - 100,
      y = 400,
      width = 200,
      height = 50,
      text = "Resume",
      listener = new ClickListener() {
        override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
          game.resumeGame()
        }
      },
      skin = game.gameView.skin
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
      skin = game.gameView.skin
    )

    stage.addActor(resumeButton)
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
    button.setX(x)
    button.setY(y)
    button.setWidth(width)
    button.setHeight(height)
    button.addListener(listener)

    button
  }
}
