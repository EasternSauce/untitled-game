package com.easternsauce.game.screen.startmenu.client

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, TextButton, TextField}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.{InputEvent, Stage}
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.easternsauce.game.CoreGame

//noinspection SameParameterValue
case class ClientStartMenuStageBuilder() {
  def build()(implicit game: CoreGame): Stage = {
    val stage = new Stage(new ScreenViewport())

    val nameField = createTextField(
      x = Gdx.graphics.getWidth / 2 - 100,
      y = 400,
      width = 200,
      height = 50,
      text = "name",
      skin = game.view.skin
    )

    val hostField = createTextField(
      x = Gdx.graphics.getWidth / 2 - 100,
      y = 320,
      width = 125,
      height = 50,
      text = "host",
      skin = game.view.skin
    )

    val portField = createTextField(
      x = Gdx.graphics.getWidth / 2 + 25,
      y = 320,
      width = 75,
      height = 50,
      text = "port",
      skin = game.view.skin
    )

    val joinButton = createButton(
      x = Gdx.graphics.getWidth / 2 - 100,
      y = 240,
      width = 200,
      height = 50,
      text = "Join game",
      listener = new ClickListener() {
        override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
          if (nameField.getText.nonEmpty) {
            game.setClientData(
              nameField.getText,
              hostField.getText,
              portField.getText
            )
            game.setGameplayScreen()
          }
        }
      },
      skin = game.view.skin
    )

    val exitButton = createButton(
      x = Gdx.graphics.getWidth / 2 - 100,
      y = 160,
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

    stage.addActor(nameField)
    stage.addActor(hostField)
    stage.addActor(portField)
    stage.addActor(joinButton)
    stage.addActor(exitButton)

    stage
  }

  private def createTextField(
      x: Int,
      y: Int,
      width: Int,
      height: Int,
      text: String,
      skin: Skin
  ): TextField = {
    val textField = new TextField("", skin)
    textField.setMessageText(text)
    textField.setX(x.toFloat)
    textField.setY(y.toFloat)
    textField.setWidth(width.toFloat)
    textField.setHeight(height.toFloat)
    textField
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
