package com.easternsauce.game.connectivity

import com.easternsauce.game.client.CoreGameClient
import com.easternsauce.game.command.{ActionsPerformCommand, OverrideGameStateCommand, RegisterClientResponseCommand}
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive
import com.esotericsoftware.kryonet.{Connection, Listener}

case class ClientListener(game: CoreGameClient) extends Listener {

  override def received(connection: Connection, obj: Any): Unit = {
    obj match {
      case OverrideGameStateCommand(gameState) =>
        game.overrideGameState(gameState)
      case ActionsPerformCommand(events) =>
        game.applyEventsToGameState(events)
      case RegisterClientResponseCommand(clientId) =>
        game.registerClient(clientId)
      case _: KeepAlive =>
    }
  }
}
