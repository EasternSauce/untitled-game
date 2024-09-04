package com.easternsauce.game.client

import com.easternsauce.game.connectivity.GameClientConnectivity
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.screen.gameplay.client.ClientGameplayScreen
import com.easternsauce.game.screen.pausemenu.client.ClientPauseMenuScreen
import com.easternsauce.game.screen.startmenu.client.ClientStartMenuScreen
import com.easternsauce.game.{CoreGame, Gameplay}
import com.esotericsoftware.kryonet.Client

case class CoreGameClient() extends CoreGame {
  implicit val game: CoreGame = this

  var clientRegistered = false

  private var _gameplay: Gameplay = _

  override protected val connectivity: GameClientConnectivity =
    GameClientConnectivity(this)

  override protected def init(): Unit = {
    gameplayScreen = ClientGameplayScreen(this)
    startMenuScreen = ClientStartMenuScreen(this)
    pauseMenuScreen = ClientPauseMenuScreen(this)

    _gameplay = Gameplay()
    _gameplay.init()
  }

  def overrideGameState(gameState: GameState): Unit =
    gameplay.gameStateHolder.gameState = gameState

  def client: Client = connectivity.endPoint

  override protected def gameplay: Gameplay = _gameplay

  override def dispose(): Unit = {
    super.dispose()
    if (client != null) {
      client.close()
    }
  }

}
