package com.easternsauce.game.client

import com.easternsauce.game.CoreGame
import com.easternsauce.game.client.screen.gameplay.ClientGameplayScreen
import com.easternsauce.game.client.screen.pausemenu.ClientPauseMenuScreen
import com.easternsauce.game.client.screen.startmenu.ClientStartMenuScreen
import com.easternsauce.game.gameview.GameScreen

case class CoreGameClient() extends CoreGame {
  protected var _gameplayScreen: GameScreen = _
  protected var _startMenuScreen: GameScreen = _
  protected var _pauseMenuScreen: GameScreen = _

  override protected def init(): Unit = {
    _gameplayScreen = ClientGameplayScreen(this)
    _gameplayScreen.init()
    _startMenuScreen = ClientStartMenuScreen(this)
    _startMenuScreen.init()
    _pauseMenuScreen = ClientPauseMenuScreen(this)
    _pauseMenuScreen.init()
  }

  override protected def gameplayScreen: GameScreen = _gameplayScreen
  override protected def startMenuScreen: GameScreen = _startMenuScreen
  override protected def pauseMenuScreen: GameScreen = _pauseMenuScreen
}
