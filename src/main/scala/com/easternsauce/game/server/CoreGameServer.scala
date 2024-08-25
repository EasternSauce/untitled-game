package com.easternsauce.game.server

import com.easternsauce.game.server.screen.gameplay.ServerGameplayScreen
import com.easternsauce.game.server.screen.pausemenu.ServerPauseMenuScreen
import com.easternsauce.game.server.screen.startmenu.ServerStartMenuScreen
import com.easternsauce.game.{CoreGame, GameStateBroadcaster, Gameplay}
import com.esotericsoftware.kryonet.{KryoSerialization, Server}
import com.twitter.chill.{Kryo, ScalaKryoInstantiator}

case class CoreGameServer() extends CoreGame {
  implicit val game: CoreGame = this

  private var _gameplay: Gameplay = _

  private val gameStateBroadcaster: GameStateBroadcaster = GameStateBroadcaster(
    this
  )

  private var clientCounter = 0

  override protected val endPoint: Server = {
    val kryo: Kryo = {
      val instantiator = new ScalaKryoInstantiator
      instantiator.setRegistrationRequired(false)
      instantiator.newKryo()
    }

    new Server(16384 * 100, 2048 * 100, new KryoSerialization(kryo))
  }

  private def server: Server = endPoint
  private val listener: ServerListener = ServerListener(this)

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
