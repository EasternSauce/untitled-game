package com.easternsauce.game

import com.badlogic.gdx.{Game, Gdx}
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamephysics.GamePhysics
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.gameview.{GameScreen, GameView}

abstract class CoreGame extends Game {
  private var clientId: Option[String] = None
  private var host: Option[String] = None
  private var port: Option[String] = None

  protected var gameplayScreen: GameScreen = _
  protected var startMenuScreen: GameScreen = _
  protected var pauseMenuScreen: GameScreen = _

  var tiledMap: GameTiledMap = _

  var gameState: GameState = _

  var gameView: GameView = _

  var gamePhysics: GamePhysics = _

  var playersToCreate: List[String] = List()

  def initScreens(): Unit

  override def create(): Unit = {
    val areaId = AreaId("area1")
    tiledMap = gamemap.GameTiledMap(areaId)
    tiledMap.init()

    gameView = GameView()
    gameView.init(this)

    gamePhysics = GamePhysics()
    gamePhysics.init(Map(areaId -> tiledMap), gameState)

    initScreens()

    setScreen(startMenuScreen)

    gameState = GameState()
  }

  def update(delta: Float): Unit = {
    gameState = gameState.update(delta, this)

    gamePhysics.updateForArea(AreaId("area1"), gameState)

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

    playersToCreate = playersToCreate.appended(clientId)
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
}
