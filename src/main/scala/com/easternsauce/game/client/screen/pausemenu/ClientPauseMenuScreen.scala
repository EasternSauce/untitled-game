package com.easternsauce.game.client.screen.pausemenu

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.{Gdx, InputMultiplexer, InputProcessor, Screen}
import com.easternsauce.game.CoreGame

case class ClientPauseMenuScreen(game: CoreGame) extends Screen {
  private var stage: Stage = _
  private var inputProcessor: InputProcessor = _

  override def show(): Unit = {
    stage = ClientPauseMenuStageBuilder().build(game)
    inputProcessor = buildInputProcessor()

    Gdx.input.setInputProcessor(inputProcessor)
  }

  private def buildInputProcessor(): InputProcessor = {
    val multiplexer = new InputMultiplexer()

    multiplexer.addProcessor(stage)
    multiplexer.addProcessor(ClientPauseMenuInputProcessor(game))

    multiplexer
  }

  override def render(delta: Float): Unit = {
    ScreenUtils.clear(0, 0, 0.2f, 1)

    stage.draw()
  }

  override def resize(width: Int, height: Int): Unit = {}

  override def pause(): Unit = {}

  override def resume(): Unit = {}

  override def hide(): Unit = {}

  override def dispose(): Unit = {}
}
