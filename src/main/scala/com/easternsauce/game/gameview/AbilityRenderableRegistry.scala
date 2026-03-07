package com.easternsauce.game.gameview

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.id.GameEntityId

/** Registry for ability renderables */
case class AbilityRenderableRegistry()
    extends RenderableRegistry[
      AbilityComponent,
      GameEntityId[AbilityComponent],
      AbilityRenderable
    ] {

  protected def entities(implicit
      game: CoreGame
  ): Map[GameEntityId[AbilityComponent], AbilityComponent] =
    game.gameState.abilityComponents

  protected def createRenderable(
      id: GameEntityId[AbilityComponent]
  )(implicit game: CoreGame): AbilityRenderable = {
    val r = AbilityRenderable(id)
    r.init()
    r
  }
}
