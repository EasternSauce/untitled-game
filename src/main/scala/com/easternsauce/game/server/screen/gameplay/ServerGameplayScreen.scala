package com.easternsauce.game.server.screen.gameplay

import com.easternsauce.game.CoreGame
import com.easternsauce.game.gameview.GameScreen

case class ServerGameplayScreen(game: CoreGame) extends GameScreen {

  override def init(): Unit = {}

  override def show(): Unit = {}

  override def render(delta: Float): Unit = {}

  override def resize(width: Int, height: Int): Unit = {}

  override def pause(): Unit = {}

  override def resume(): Unit = {}

  override def hide(): Unit = {}

  override def dispose(): Unit = {}
}
