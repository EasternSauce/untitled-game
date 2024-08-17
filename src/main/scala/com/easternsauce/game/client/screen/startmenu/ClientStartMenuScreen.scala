package com.easternsauce.game.client.screen.startmenu

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.{Gdx, Screen}
import com.easternsauce.game.CoreGame

case class ClientStartMenuScreen(game: CoreGame) extends Screen {
  private var stage: Stage = _

  override def show(): Unit = {
    stage = ClientStartMenuStageBuilder().build(game)

    Gdx.input.setInputProcessor(stage)
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
