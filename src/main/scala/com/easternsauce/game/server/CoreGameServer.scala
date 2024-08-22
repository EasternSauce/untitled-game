package com.easternsauce.game.server

import com.easternsauce.game.server.screen.gameplay.ServerGameplayScreen
import com.easternsauce.game.server.screen.pausemenu.ServerPauseMenuScreen
import com.easternsauce.game.server.screen.startmenu.ServerStartMenuScreen
import com.easternsauce.game.{CoreGame, Gameplay}

case class CoreGameServer() extends CoreGame {
  implicit val game: CoreGame = this

  private var _gameplay: Gameplay = _

  override protected def init(): Unit = {
    gameplayScreen = ServerGameplayScreen()
    startMenuScreen = ServerStartMenuScreen()
    pauseMenuScreen = ServerPauseMenuScreen()

    _gameplay = Gameplay()
    _gameplay.init()
  }

  override protected def gameplay: Gameplay = _gameplay
}
