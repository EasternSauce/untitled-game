package com.easternsauce.game.client.screen.gameplay

import com.badlogic.gdx.Gdx
import com.easternsauce.game.client.CoreGameClient
import com.easternsauce.game.gameview.GameScreen

case class ClientGameplayScreen(game: CoreGameClient) extends GameScreen {
  implicit private val _game: CoreGameClient = game

  private var clientConnected = false

  private var inputProcessor: ClientGameplayInputProcessor = _

  override def init(): Unit = {
    inputProcessor = ClientGameplayInputProcessor()
  }

  override def show(): Unit = {
    Gdx.input.setInputProcessor(inputProcessor)

    if (!clientConnected) {
      game.client.start()
      game.client.connect(
        50000,
        game.clientData.host.getOrElse("localhost"),
        game.clientData.port.map(_.toInt).getOrElse(54555),
        54777
      )

      game.client.addListener(game.listener)
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
