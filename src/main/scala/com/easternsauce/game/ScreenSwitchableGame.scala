package com.easternsauce.game

import com.badlogic.gdx.Game
import com.easternsauce.game.gameview.GameScreen

abstract class ScreenSwitchableGame extends Game {
  protected def gameplayScreen: GameScreen
  protected def startMenuScreen: GameScreen
  protected def pauseMenuScreen: GameScreen

  private var _clientData: ClientData = _

  def joinGame(clientId: String, host: String, port: String): Unit = {
    if (clientId.nonEmpty) {
      _clientData.clientId = Some(clientId)
    }
    if (host.nonEmpty) {
      _clientData.host = Some(host)
    }
    if (port.nonEmpty) {
      _clientData.port = Some(port)
    }

    _clientData.clientId.foreach(gameplay.schedulePlayerToCreate(_))

    setScreen(gameplayScreen)
  }

  def resumeGame(): Unit = {
    setScreen(gameplayScreen)
  }

  def pauseGame(): Unit = {
    setScreen(pauseMenuScreen)
  }

  def clientData: ClientData = _clientData
}
