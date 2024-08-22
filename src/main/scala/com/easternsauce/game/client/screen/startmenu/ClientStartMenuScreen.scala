package com.easternsauce.game.client.screen.startmenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.ScreenUtils
import com.easternsauce.game.CoreGame
import com.easternsauce.game.gameview.GameScreen

case class ClientStartMenuScreen()(implicit game: CoreGame) extends GameScreen {
  private var stage: Stage = _

  override def init(): Unit = {
    stage = ClientStartMenuStageBuilder().build()
  }

  override def show(): Unit = {
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
