package com.easternsauce.game.server

import com.easternsauce.game.core.CoreGame

case class ClientConnectionManager() {
  private var _clientConnectionIds: Map[String, Int] = Map()

  def registerClient(clientId: String, connectionId: Int)(implicit
      game: CoreGame
  ): Unit = {
    _clientConnectionIds = _clientConnectionIds.updated(clientId, connectionId)

    game.gameplay.entityCreator.schedulePlayerToCreate(clientId)
  }

  def unregisterClient(clientId: String, connectionId: Int): Unit = {
    _clientConnectionIds = _clientConnectionIds.removed(clientId)
  }

  def clientConnectionIds: Map[String, Int] = {
    Map.from(_clientConnectionIds)
  }
}
