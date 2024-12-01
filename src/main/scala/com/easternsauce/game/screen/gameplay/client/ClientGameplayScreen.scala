package com.easternsauce.game.screen.gameplay.client

import com.badlogic.gdx.Gdx
import com.easternsauce.game.client.CoreGameClientBase
import com.easternsauce.game.command.RegisterClientRequestCommand
import com.easternsauce.game.gameview.GameScreen

case class ClientGameplayScreen(game: CoreGameClientBase) extends GameScreen {
  implicit private val _game: CoreGameClientBase = game

  private var clientConnected = false

  private var inputProcessor: ClientGameplayInputProcessor = _

  override def init(): Unit = {
    inputProcessor = ClientGameplayInputProcessor()
  }

  override def show(): Unit = {
    Gdx.input.setInputProcessor(inputProcessor)

    if (!game.isOffline && !clientConnected) {
      game.client.start()
      game.client.connect(
        50000,
        game.clientData.host.getOrElse("localhost"),
        game.clientData.port.map(_.toInt).getOrElse(54555),
        54777
      )

      game.client.addListener(game.listener)

      game.client.sendTCP(
        RegisterClientRequestCommand(game.clientData.clientId)
      )
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
