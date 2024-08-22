package com.easternsauce.game

import com.badlogic.gdx.Game
import com.easternsauce.game.gameview.GameScreen

abstract class ScreenSwitchableGame extends Game {

  protected var gameplayScreen: GameScreen = _
  protected var startMenuScreen: GameScreen = _
  protected var pauseMenuScreen: GameScreen = _
  protected var _clientData: ClientData = _
  protected var clientData: ClientData = _

  def joinGame(clientId: String, host: String, port: String): Unit = {
    if (clientId.nonEmpty) {
      clientData.clientId = Some(clientId)
    }
    if (host.nonEmpty) {
      clientData.host = Some(host)
    }
    if (port.nonEmpty) {
      clientData.port = Some(port)
    }

    clientData.clientId.foreach(schedulePlayerToCreate)

    setScreen(gameplayScreen)
  }

  def resumeGame(): Unit = {
    setScreen(gameplayScreen)
  }

  def pauseGame(): Unit = {
    setScreen(pauseMenuScreen)
  }

  protected def init(): Unit

  def schedulePlayerToCreate(clientId: String): Unit

}
