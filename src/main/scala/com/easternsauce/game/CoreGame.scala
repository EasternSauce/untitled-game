package com.easternsauce.game

import com.badlogic.gdx.Gdx
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamephysics.GamePhysics
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.gameview.GameView

abstract class CoreGame extends ScreenSwitchableGame {
  private var gameplay: Gameplay = _

  protected def init(): Unit

  override def create(): Unit = {
    clientData = ClientData()

    gameplay = Gameplay(this)
    gameplay.init()

    init()

    setScreen(startMenuScreen)
  }

  def update(delta: Float): Unit = {
    gameplay.update(delta)
  }

  def close(): Unit = {
    Gdx.app.exit()
  }

  def clientCreatureId: Option[GameEntityId[Creature]] = {
    clientData.clientId.map(GameEntityId[Creature])
  }

  def clientCreatureAreaId: Option[AreaId] = {
    clientCreatureId
      .filter(gameState.creatures.contains(_))
      .map(gameState.creatures(_))
      .map(_.currentAreaId)
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
