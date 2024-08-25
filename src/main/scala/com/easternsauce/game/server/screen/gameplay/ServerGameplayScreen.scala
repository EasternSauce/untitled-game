package com.easternsauce.game.server.screen.gameplay

import com.easternsauce.game.gameview.GameScreen
import com.easternsauce.game.server.CoreGameServer

case class ServerGameplayScreen(game: CoreGameServer) extends GameScreen {
  private var serverRunning = false

  override def init(): Unit = {}

  override def show(): Unit = {
    if (!serverRunning) {
      new Thread(new Runnable() {
        override def run(): Unit = {
          game.runServer()
        }
      }).start()
      serverRunning = true
    }
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
