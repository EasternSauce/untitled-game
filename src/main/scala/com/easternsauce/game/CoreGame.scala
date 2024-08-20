package com.easternsauce.game

import com.badlogic.gdx.{Game, Gdx}
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamephysics.GamePhysics
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.gameview.{GameScreen, GameView}

abstract class CoreGame extends Game {
  private var clientId: Option[String] = _
  private var host: Option[String] = _
  private var port: Option[String] = _

  protected var gameplayScreen: GameScreen = _
  protected var startMenuScreen: GameScreen = _
  protected var pauseMenuScreen: GameScreen = _

  private var gameplay: Gameplay = _

  def initScreens(): Unit

  override def create(): Unit = {
    clientId = None
    host = None
    port = None

    gameplay = Gameplay(this)
    gameplay.init()

    initScreens()

    setScreen(startMenuScreen)
  }

  def update(delta: Float): Unit = {
    gameplay.update(delta)
  }

  def joinGame(clientId: String, host: String, port: String): Unit = {
    if (clientId.nonEmpty) {
      this.clientId = Some(clientId)
    }
    if (host.nonEmpty) {
      this.host = Some(host)
    }
    if (port.nonEmpty) {
      this.port = Some(port)
    }

    if (this.clientId.nonEmpty)
      gameplay.schedulePlayerToCreate(this.clientId.get)

    setScreen(gameplayScreen)
  }

  def resumeGame(): Unit = {
    setScreen(gameplayScreen)
  }

  def pauseGame(): Unit = {
    setScreen(pauseMenuScreen)
  }

  def close(): Unit = {
    Gdx.app.exit()
  }

  def clientCreatureId: Option[GameEntityId[Creature]] = {
    clientId.map(GameEntityId[Creature])
  }

  def view: GameView = gameplay.gameView
  def physics: GamePhysics = gameplay.gamePhysics
  def gameState: GameState = gameplay.gameState
  def playersToCreate: List[String] = gameplay.playersToCreate
  def clearPlayersToCreate(): Unit = gameplay.playersToCreate = List()
  def gameTiledMaps: Map[AreaId, GameTiledMap] = gameplay.gameTiledMaps
  def keyHeld(key: Int): Boolean = gameplay.keysHeld.getOrElse(key, false)
  def setKeyHeld(key: Int, value: Boolean): Unit =
    gameplay.setKeyHeld(key, value)
}
