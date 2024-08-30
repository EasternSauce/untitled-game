package com.easternsauce.game.server

import com.easternsauce.game.connectivity.GameServerConnectivity
import com.easternsauce.game.screen.gameplay.server.ServerGameplayScreen
import com.easternsauce.game.screen.pausemenu.server.ServerPauseMenuScreen
import com.easternsauce.game.screen.startmenu.server.ServerStartMenuScreen
import com.easternsauce.game.{CoreGame, GameStateBroadcaster, Gameplay}
import com.esotericsoftware.kryonet.Server

case class CoreGameServer() extends CoreGame {
  implicit val game: CoreGame = this

  override protected val connectivity: GameServerConnectivity =
    GameServerConnectivity(this)

  private var _gameplay: Gameplay = _

  private val gameStateBroadcaster: GameStateBroadcaster = GameStateBroadcaster(
    this
  )

  private var clientCounter = 0

  private def server: Server = connectivity.endPoint

  def runServer(): Unit = {
    server.start()
    server.bind(54555, 54777)

    server.addListener(listener)
  }

  override protected def init(): Unit = {
    gameplayScreen = ServerGameplayScreen(this)
    startMenuScreen = ServerStartMenuScreen(this)
    pauseMenuScreen = ServerPauseMenuScreen(this)

    _gameplay = Gameplay()
    _gameplay.init()
  }

  def generateNewClientId(): String = {
    val id = "client" + clientCounter

    clientCounter = clientCounter + 1

    id
  }

  override protected def gameplay: Gameplay = _gameplay

  private var _clientConnectionIds: Map[String, Int] = Map()

  def registerClient(clientId: String, connectionId: Int): Unit = {
    _clientConnectionIds = _clientConnectionIds.updated(clientId, connectionId)

    gameplay.schedulePlayerToCreate(clientId)
  }

  def unregisterClient(clientId: String, connectionId: Int): Unit = {
    _clientConnectionIds = _clientConnectionIds.removed(clientId)
  }

  def clientConnectionIds: Map[String, Int] = {
    Map.from(_clientConnectionIds)
  }

}
