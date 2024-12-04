package com.easternsauce.game.gameview

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

trait Renderable {
  def pos()(implicit game: CoreGame): Vector2f

  def areaId(gameState: GameState): AreaId

  def hasRenderPriority(gameState: GameState): Boolean

  def renderCreature(batch: GameSpriteBatch, worldCameraPos: Vector2f)(implicit
      game: CoreGame
  ): Unit
}
