package com.easternsauce.game.server

import com.easternsauce.game.CoreGame
import com.easternsauce.game.server.screen.gameplay.ServerGameplayScreen
import com.easternsauce.game.server.screen.pausemenu.ServerPauseMenuScreen
import com.easternsauce.game.server.screen.startmenu.ServerStartMenuScreen

case class CoreGameServer() extends CoreGame {
  override def initScreens(): Unit = {
    gameplayScreen = ServerGameplayScreen(this)
    startMenuScreen = ServerStartMenuScreen(this)
    pauseMenuScreen = ServerPauseMenuScreen(this)
  }
}
