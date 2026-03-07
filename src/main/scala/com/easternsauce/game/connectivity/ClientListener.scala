package com.easternsauce.game.connectivity

import com.easternsauce.game.client.CoreGameClientBase
import com.easternsauce.game.command.{
  ActionsPerformCommand,
  OverrideGameStateCommand,
  RegisterClientResponseCommand
}
import com.easternsauce.game.core.CoreGame
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive
import com.esotericsoftware.kryonet.{Connection, Listener}

case class ClientListener(game: CoreGameClientBase) extends Listener {
  private implicit val _game: CoreGame = game

  override def received(connection: Connection, obj: Any): Unit = {
    obj match {
      case OverrideGameStateCommand(gameState) =>
        game.scheduleOverrideGameState(gameState)
      case ActionsPerformCommand(events) =>
        game.sendLocalEvents(events)
      case RegisterClientResponseCommand(clientId) =>
        game.registerClient(clientId)
      case _: KeepAlive =>
    }
  }
}
