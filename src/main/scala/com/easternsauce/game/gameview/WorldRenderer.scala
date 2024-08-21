package com.easternsauce.game.gameview

import com.easternsauce.game.CoreGame
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.math.Vector2f

case class WorldRenderer() {
  private var creatureRenderer: CreatureRenderer = _

  def init(): Unit = {
    creatureRenderer = CreatureRenderer()
    creatureRenderer.init()
  }

  def drawCurrentWorld(
      spriteBatchHolder: SpriteBatchHolder,
      worldCameraPos: Vector2f,
      game: CoreGame
  ): Unit = {
    spriteBatchHolder.worldSpriteBatch.begin()

    val clientCreatureAreaId = game.clientCreatureId
      .filter(game.gameState.creatures.contains(_))
      .map(game.gameState.creatures(_))
      .map(_.currentAreaId)

    clientCreatureAreaId.foreach(areaId =>
      renderWorldElementsByPriority(
        spriteBatchHolder.worldSpriteBatch,
        worldCameraPos,
        game.gameTiledMaps(areaId),
        game.gameState
      )
    )

    spriteBatchHolder.worldSpriteBatch.end()

    spriteBatchHolder.worldTextSpriteBatch.begin()

    spriteBatchHolder.worldTextSpriteBatch.end()
  }

  private def renderWorldElementsByPriority(
      worldSpriteBatch: GameSpriteBatch,
      worldCameraPos: Vector2f,
      gameTiledMap: GameTiledMap,
      gameState: GameState
  ): Unit = {

    gameTiledMap.render(
      worldSpriteBatch,
      worldCameraPos,
      gameState
    )

    renderDynamicElements(
      worldSpriteBatch,
      worldCameraPos,
      gameState
    )
  }

  private def renderDynamicElements(
      worldSpriteBatch: GameSpriteBatch,
      worldCameraPos: Vector2f,
      gameState: GameState
  ): Unit = {
    creatureRenderer.renderAliveCreatures(
      worldSpriteBatch,
      worldCameraPos,
      gameState
    )
  }

  def update(gameState: GameState): Unit = {
    creatureRenderer.updateRenderables(gameState)
  }
}
