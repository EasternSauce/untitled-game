package com.easternsauce.game.gameview

import com.easternsauce.game.gamestate.{AreaId, GameState}
import com.easternsauce.game.math.Vector2f

trait Renderable {
  def pos(gameState: GameState): Vector2f

  def areaId(gameState: GameState): AreaId

  def renderPriority(gameState: GameState): Boolean

  def render(
      batch: GameSpriteBatch,
      worldCameraPos: Vector2f,
      gameState: GameState
  ): Unit
}
