package com.easternsauce.game.screen.gameplay.server

import com.badlogic.gdx.Gdx
import com.easternsauce.game.gameview.GameScreen
import com.easternsauce.game.server.CoreGameServer

case class ServerGameplayScreen(game: CoreGameServer) extends GameScreen {
  implicit private val _game: CoreGameServer = game

  private var serverRunning = false

  private var inputProcessor: ServerGameplayInputProcessor = _

  override def init(): Unit = {
    inputProcessor = ServerGameplayInputProcessor()
  }

  override def show(): Unit = {
    Gdx.input.setInputProcessor(inputProcessor)

    if (!serverRunning) {
      game.runServer()
      serverRunning = true

      game.startBroadcaster()
    }
  }

  override def render(delta: Float): Unit = {
    game.update(delta)
  }

  override def resize(width: Int, height: Int): Unit = {
    game.gameplay.view.resize(width, height)
  }

  override def pause(): Unit = {}

  override def resume(): Unit = {}

  override def hide(): Unit = {}

  override def dispose(): Unit = {}
}
