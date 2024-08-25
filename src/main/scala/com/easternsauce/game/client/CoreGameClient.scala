package com.easternsauce.game.client

import com.easternsauce.game.client.screen.gameplay.ClientGameplayScreen
import com.easternsauce.game.client.screen.pausemenu.ClientPauseMenuScreen
import com.easternsauce.game.client.screen.startmenu.ClientStartMenuScreen
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.{Constants, CoreGame, Gameplay}
import com.esotericsoftware.kryonet.{Client, KryoSerialization}
import com.twitter.chill.{Kryo, ScalaKryoInstantiator}

case class CoreGameClient() extends CoreGame {
  implicit val game: CoreGame = this

  var clientId: Option[String] = None
  var clientRegistered = false

  override protected val endPoint: Client = {
    if (!Constants.OfflineMode) {
      val kryo: Kryo = {
        val instantiator = new ScalaKryoInstantiator
        instantiator.setRegistrationRequired(false)
        instantiator.newKryo()

      }
      new Client(8192 * 100, 2048 * 100, new KryoSerialization(kryo))
    } else {
      null
    }
  }

  private var _gameplay: Gameplay = _

  override protected def init(): Unit = {
    gameplayScreen = ClientGameplayScreen(this)
    startMenuScreen = ClientStartMenuScreen(this)
    pauseMenuScreen = ClientPauseMenuScreen(this)

    _gameplay = Gameplay()
    _gameplay.init()
  }

  def overrideGameState(gameState: GameState): Unit =
    gameplay.gameStateHolder.gameState = gameState

  def client: Client = endPoint
  val listener: ClientListener = ClientListener(this)

  override protected def gameplay: Gameplay = _gameplay
}
