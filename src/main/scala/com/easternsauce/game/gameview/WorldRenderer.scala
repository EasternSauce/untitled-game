package com.easternsauce.game.gameview

import com.easternsauce.game.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.id.AreaId
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

    renderWorldElementsByPriority(
      spriteBatches.worldSpriteBatch,
      worldCameraPos,
      Some(game.gameState.currentAreaId),
      game.gameState
    )

    spriteBatches.worldSpriteBatch.end()

    spriteBatches.worldTextSpriteBatch.begin()

    spriteBatches.worldTextSpriteBatch.end()
  }

  private def renderWorldElementsByPriority(
      worldSpriteBatch: GameSpriteBatch,
      worldCameraPos: Vector2f,
      currentAreaId: Option[AreaId],
      gameState: GameState
  ): Unit = {
    if (currentAreaId.isDefined) {
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
