package com.easternsauce.game.screen.gameplay.server

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

      game.startBroadcaster()
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
