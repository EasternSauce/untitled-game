package com.easternsauce.game.client

import com.easternsauce.game.client.screen.gameplay.ClientGameplayScreen
import com.easternsauce.game.client.screen.pausemenu.ClientPauseMenuScreen
import com.easternsauce.game.client.screen.startmenu.ClientStartMenuScreen
import com.easternsauce.game.{CoreGame, Gameplay}

case class CoreGameClient() extends CoreGame {
  implicit val game: CoreGame = this

  private var _gameplay: Gameplay = _

  override protected def init(): Unit = {
    gameplayScreen = ClientGameplayScreen()
    startMenuScreen = ClientStartMenuScreen()
    pauseMenuScreen = ClientPauseMenuScreen()

    _gameplay = Gameplay()
    _gameplay.init()
  }

  override protected def gameplay: Gameplay = _gameplay
}
