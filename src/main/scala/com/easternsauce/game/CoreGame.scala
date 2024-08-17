package com.easternsauce.game

import com.badlogic.gdx.{Game, Gdx}
import com.easternsauce.game.gamemap.GameMapRenderer
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gameview.{GameScreen, GameView}

abstract class CoreGame extends Game {
  private var clientId: Option[String] = None
  private var host: Option[String] = None
  private var port: Option[String] = None

  protected var gameplayScreen: GameScreen = _
  protected var startMenuScreen: GameScreen = _
  protected var pauseMenuScreen: GameScreen = _

  var mapRenderer: GameMapRenderer = _

  var gameState: GameState = _

  var gameView: GameView = _

  def initScreens(): Unit

  override def create(): Unit = {
    val areaId = AreaId("area1")
    mapRenderer = gamemap.GameMapRenderer(areaId)
    mapRenderer.init()

    gameView = GameView()
    gameView.init(this)

    initScreens()

    setScreen(startMenuScreen)

    gameState = GameState()
  }

  def update(delta: Float): Unit = {
    gameState = gameState.update(delta, this)

    gameView.update(delta, this)
    gameView.render(delta, this)
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
}
