package com.easternsauce.game.client.screen.gameplay

import com.badlogic.gdx.Gdx
import com.easternsauce.game.CoreGame
import com.easternsauce.game.gameview.GameScreen

case class ClientGameplayScreen()(implicit game: CoreGame) extends GameScreen {
  private var inputProcessor: ClientGameplayInputProcessor = _

  override def init(): Unit = {
    inputProcessor = ClientGameplayInputProcessor()
  }

  override def show(): Unit = {
    Gdx.input.setInputProcessor(inputProcessor)
  }

  override def render(delta: Float): Unit = {
    game.update(delta)
  }

  override def resize(width: Int, height: Int): Unit = {
    game.view.resize(width, height)
  }

  override def pause(): Unit = {}

  override def resume(): Unit = {}

  override def hide(): Unit = {}

  override def dispose(): Unit = {}

}
