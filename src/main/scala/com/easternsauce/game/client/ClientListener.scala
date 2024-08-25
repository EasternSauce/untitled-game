package com.easternsauce.game.client

import com.easternsauce.game.GameStateHolder
import com.easternsauce.game.command.{ActionsPerformCommand, RegisterClientResponseCommand}
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive
import com.esotericsoftware.kryonet.{Connection, Listener}

case class ClientListener(game: CoreGameClient) extends Listener {

  override def received(connection: Connection, obj: Any): Unit = {
    obj match {
      case GameStateHolder(gameState) =>
        game.overrideGameState(gameState)
      case ActionsPerformCommand(actions) =>
        actions.foreach(game.applyEvent)
      case RegisterClientResponseCommand(clientId) =>
        game.clientId = Some(clientId)
        game.clientRegistered = true
      case _: KeepAlive =>
    }
  }
}
