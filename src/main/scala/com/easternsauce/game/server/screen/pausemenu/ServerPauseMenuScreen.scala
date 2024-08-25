package com.easternsauce.game.server.screen.pausemenu

import com.easternsauce.game.gameview.GameScreen
import com.easternsauce.game.server.CoreGameServer

case class ServerPauseMenuScreen(game: CoreGameServer) extends GameScreen {

  override def init(): Unit = {}

  override def show(): Unit = {}

  override def render(delta: Float): Unit = {}

  override def resize(width: Int, height: Int): Unit = {}

  override def pause(): Unit = {}

  override def resume(): Unit = {}

  override def hide(): Unit = {}

  override def dispose(): Unit = {}
}
