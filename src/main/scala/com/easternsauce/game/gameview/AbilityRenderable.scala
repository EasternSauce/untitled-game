package com.easternsauce.game.gameview

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.gamestate.projectile.ProjectileComponent
import com.easternsauce.game.math.Vector2f

case class AbilityRenderable(projectileComponentId: GameEntityId[ProjectileComponent])
    extends Renderable {
  private var animation: ProjectileAnimation = _

  def init()(implicit game: CoreGame): Unit = {
    animation = ProjectileAnimation(projectileComponentId)

    animation.init()
  }

  override def pos()(implicit game: CoreGame): Vector2f = {
    val ability = game.gameState.projectileComponents(projectileComponentId)

    ability.pos
  }

  override def areaId(gameState: GameState): AreaId = {
    val ability = gameState.projectileComponents(projectileComponentId)

    ability.params.currentAreaId
  }

  override def render(batch: RenderBatch, worldCameraPos: Vector2f)(implicit
      game: CoreGame
  ): Unit = {
    animation.render(batch)
  }

  override def hasRenderPriority(gameState: GameState): Boolean = {
    true
  }
}
