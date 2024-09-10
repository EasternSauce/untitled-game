package com.easternsauce.game.connectivity

import com.easternsauce.game.GameStateHolder
import com.easternsauce.game.client.CoreGameClient
import com.easternsauce.game.command.{ActionsPerformCommand, RegisterClientResponseCommand}
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive
import com.esotericsoftware.kryonet.{Connection, Listener}

case class ClientListener(game: CoreGameClient) extends Listener {

  override def received(connection: Connection, obj: Any): Unit = {
    obj match {
      case GameStateHolder(gameState) =>
        game.overrideGameState(gameState)
      case ActionsPerformCommand(events) =>
        game.applyEvents(events)
      case RegisterClientResponseCommand(clientId) =>
        game.clientData.clientId = Some(clientId)
        game.clientRegistered = true
      case _: KeepAlive =>
    }
  }
}
