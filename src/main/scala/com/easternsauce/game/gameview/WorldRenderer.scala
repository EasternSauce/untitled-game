package com.easternsauce.game.gameview

import com.easternsauce.game.CoreGame
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.math.Vector2f

case class WorldRenderer() {
  private var creatureRenderers: CreatureRenderers = _

  def init(game: CoreGame): Unit = {
    creatureRenderers = CreatureRenderers()
    creatureRenderers.init(game.gameState)

  }

  def drawCurrentWorld(
      spriteBatches: SpriteBatches,
      worldCameraPos: Vector2f,
      game: CoreGame
  ): Unit = {
    spriteBatches.worldSpriteBatch.begin()

    val clientCreatureAreaId = game.clientCreatureId
      .filter(game.gameState.creatures.contains(_))
      .map(game.gameState.creatures(_))
      .map(_.currentAreaId)

    renderWorldElementsByPriority(
      spriteBatches.worldSpriteBatch,
      worldCameraPos,
      clientCreatureAreaId.map(game.gameTiledMaps(_)),
      game.gameState
    )

    spriteBatches.worldSpriteBatch.end()

    spriteBatches.worldTextSpriteBatch.begin()

    spriteBatches.worldTextSpriteBatch.end()
  }

  private def renderWorldElementsByPriority(
      worldSpriteBatch: GameSpriteBatch,
      worldCameraPos: Vector2f,
      gameTiledMap: Option[GameTiledMap],
      gameState: GameState
  ): Unit = {
    if (gameTiledMap.isDefined) {
      gameTiledMap.get.render(
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

  }

  private def renderDynamicElements(
      worldSpriteBatch: GameSpriteBatch,
      worldCameraPos: Vector2f,
      gameState: GameState
  ): Unit = {
    val aliveCreatureRenderables =
      creatureRenderers.getRenderersForAliveCreatures(gameState)

    aliveCreatureRenderables
      .foreach(_.render(worldSpriteBatch, worldCameraPos, gameState))
  }

  def update(gameState: GameState): Unit = {
    creatureRenderers.update(gameState)
  }
}
