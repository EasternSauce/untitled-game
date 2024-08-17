package com.easternsauce.game

import com.easternsauce.game.gamestate.GameState

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
