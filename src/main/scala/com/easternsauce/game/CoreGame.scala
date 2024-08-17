package com.easternsauce.game

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.{Game, Gdx, Screen}
import com.easternsauce.game.gamestate.GameState

abstract class CoreGame extends Game {
  private var clientId: Option[String] = None
  private var host: Option[String] = None
  private var port: Option[String] = None

  var gameplayScreen: Screen = _
  var startMenuScreen: Screen = _
  var pauseMenuScreen: Screen = _

  var mapRenderer: GameMapRenderer = _
  var skin: Skin = _

  var gameState: GameState = _

  var spriteBatches :SpriteBatches = _
  var viewportManager: ViewportManager = _

  override def create(): Unit = {
    val areaId = AreaId("area1")
    mapRenderer = GameMapRenderer(areaId)
    mapRenderer.init()

    skin = new Skin(Gdx.files.internal("assets/ui/skin/uiskin.json"))

    initScreens()

    setScreen(startMenuScreen)

    spriteBatches = SpriteBatches()
    spriteBatches.worldSpriteBatch.init()
    spriteBatches.worldTextSpriteBatch.init()
    spriteBatches.hudBatch.init()

    viewportManager = ViewportManager()
    viewportManager.init()

    gameState = GameState()
  }

  def initScreens(): Unit

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
