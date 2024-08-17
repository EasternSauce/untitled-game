package com.easternsauce.game.client

import com.easternsauce.game.CoreGame
import com.easternsauce.game.client.screen.gameplay.ClientGameplayScreen
import com.easternsauce.game.client.screen.pausemenu.ClientPauseMenuScreen
import com.easternsauce.game.client.screen.startmenu.ClientStartMenuScreen

case class CoreGameClient() extends CoreGame {
  override def initScreens(): Unit = {
    gameplayScreen = ClientGameplayScreen(this)
    startMenuScreen = ClientStartMenuScreen(this)
    pauseMenuScreen = ClientPauseMenuScreen(this)
  }
}
