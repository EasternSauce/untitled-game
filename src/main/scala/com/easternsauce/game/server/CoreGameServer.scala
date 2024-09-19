package com.easternsauce.game.server

import com.easternsauce.game.Constants
import com.easternsauce.game.command.ActionsPerformCommand
import com.easternsauce.game.connectivity.GameServerConnectivity
import com.easternsauce.game.core.{CoreGame, Gameplay}
import com.easternsauce.game.gamestate.event.GameStateEvent
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gameview.GameScreen
import com.easternsauce.game.screen.gameplay.server.ServerGameplayScreen
import com.easternsauce.game.screen.pausemenu.server.ServerPauseMenuScreen
import com.easternsauce.game.screen.startmenu.server.ServerStartMenuScreen
import com.esotericsoftware.kryonet.Server

case class CoreGameServer() extends CoreGame {
  implicit val game: CoreGame = this

  private val gameStateBroadcaster: GameStateBroadcaster = GameStateBroadcaster(
    this
  )

  private var _gameplay: Gameplay = _
  private var _connectivity: GameServerConnectivity = _

  private var clientCounter = 0

  private var _clientConnectionIds: Map[String, Int] = Map()

  private var _gameplayScreen: GameScreen = _
  private var _startMenuScreen: GameScreen = _
  private var _pauseMenuScreen: GameScreen = _

  override protected def init(): Unit = {
    _gameplayScreen = ServerGameplayScreen(this)
    _startMenuScreen = ServerStartMenuScreen(this)
    _pauseMenuScreen = ServerPauseMenuScreen(this)

    _gameplay = Gameplay()
    _gameplay.init()

    _connectivity = GameServerConnectivity(this)
  }

  override def update(delta: Float): Unit = {
    val areaIds = Constants.MapAreaNames.map(AreaId)

    handleInputs()

    areaIds.foreach(gameplay.updateForArea(_, delta))
    gameplay.renderForArea(Constants.DefaultAreaId, delta)

    processEvents()
  }

  private def processEvents(): Unit = {
    gameplay.applyEventsToGameState(gameEventProcessor.allEvents)

    gameEventProcessor.clearEventQueues()
  }

  def sendCommandToAllClients(command: ActionsPerformCommand): Unit = {
    server.sendToAllTCP(command)
  }

  def runServer(): Unit = {
    new Thread(new Runnable() {
      override def run(): Unit = {
        server.start()
        server.bind(54555, 54777)

        server.addListener(listener)
      }
    }).start()
  }

  def generateNewClientId(): String = {
    val id = "client" + clientCounter

    clientCounter = clientCounter + 1

    id
  }

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

  def startBroadcaster(): Unit = {
    gameStateBroadcaster.start(server)
  }

  override protected def handleInputs(): Unit = {}

  override def sendBroadcastEvents(events: List[GameStateEvent]): Unit = {}

  override def gameplay: Gameplay = _gameplay
  override protected def connectivity: GameServerConnectivity = _connectivity
  private def server: Server = connectivity.endPoint

  override protected def gameplayScreen: GameScreen = _gameplayScreen
  override protected def startMenuScreen: GameScreen = _startMenuScreen
  override protected def pauseMenuScreen: GameScreen = _pauseMenuScreen

  override def dispose(): Unit = {
    super.dispose()
    server.stop()

    gameStateBroadcaster.stop()
  }

}
