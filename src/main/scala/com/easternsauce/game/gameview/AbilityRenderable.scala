package com.easternsauce.game.gameview

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

case class AbilityRenderable(abilityId: GameEntityId[AbilityComponent])
    extends Renderable {
  private var animation: AbilityAnimation = _

  def init()(implicit game: CoreGame): Unit = {
    animation = AbilityAnimation(abilityId)

    animation.init()
  }

  override def pos()(implicit game: CoreGame): Vector2f = {
    val ability = game.gameState.abilities(abilityId)

    ability.pos
  }

  override def areaId(gameState: GameState): AreaId = {
    val ability = gameState.abilities(abilityId)

    ability.params.currentAreaId
  }

  override def render(batch: GameSpriteBatch, worldCameraPos: Vector2f)(implicit
      game: CoreGame
  ): Unit = {
    animation.render(batch)
  }

  override def renderPriority(gameState: GameState): Boolean = {
    true
  }
}
