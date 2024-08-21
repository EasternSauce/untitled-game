package com.easternsauce.game.server

import com.easternsauce.game.CoreGame
import com.easternsauce.game.gameview.GameScreen
import com.easternsauce.game.server.screen.gameplay.ServerGameplayScreen
import com.easternsauce.game.server.screen.pausemenu.ServerPauseMenuScreen
import com.easternsauce.game.server.screen.startmenu.ServerStartMenuScreen

case class CoreGameServer() extends CoreGame {
  protected var _gameplayScreen: GameScreen = _
  protected var _startMenuScreen: GameScreen = _
  protected var _pauseMenuScreen: GameScreen = _

  override protected def init(): Unit = {
    _gameplayScreen = ServerGameplayScreen(this)
    _gameplayScreen.init()
    _startMenuScreen = ServerStartMenuScreen(this)
    _startMenuScreen.init()
    _pauseMenuScreen = ServerPauseMenuScreen(this)
    _pauseMenuScreen.init()
  }

  override protected def gameplayScreen: GameScreen = _gameplayScreen
  override protected def startMenuScreen: GameScreen = _startMenuScreen
  override protected def pauseMenuScreen: GameScreen = _pauseMenuScreen
}
