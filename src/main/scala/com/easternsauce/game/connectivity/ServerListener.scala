package com.easternsauce.game.connectivity

import com.easternsauce.game.command.{ActionsPerformCommand, ActionsPerformRequestCommand, RegisterClientRequestCommand, RegisterClientResponseCommand}
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.event.PlayerDisconnectEvent
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.server.CoreGameServer
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive
import com.esotericsoftware.kryonet.{Connection, Listener}

case class ServerListener(game: CoreGameServer) extends Listener {
  override def disconnected(connection: Connection): Unit = {

    val reverseMap = for ((k, v) <- game.clientConnectionIds) yield (v, k)

    if (reverseMap.contains(connection.getID)) {
      val disconnectedCreatureId = reverseMap(connection.getID)
      val playerDisconnectEvent = PlayerDisconnectEvent(
        GameEntityId[Creature](disconnectedCreatureId)
      )

      game.sendLocalEvents(List(playerDisconnectEvent))

      game.sendCommandToAllClients(
        ActionsPerformCommand(List(playerDisconnectEvent))
      )

      game.unregisterClient(disconnectedCreatureId, connection.getID)
    }
  }

  override def received(connection: Connection, obj: Any): Unit = {
    obj match {
      case RegisterClientRequestCommand(maybeClientId) =>
        val clientId = maybeClientId.getOrElse(game.generateNewClientId())
        game.registerClient(clientId, connection.getID)
        connection.sendTCP(RegisterClientResponseCommand(clientId))
      case ActionsPerformRequestCommand(events) =>
        game.applyEventsToGameState(events)

        game.sendCommandToAllClientsExcept(
          connection.getID,
          ActionsPerformCommand(events)
        )
      case _: KeepAlive =>
    }
  }
}
