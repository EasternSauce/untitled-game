package com.easternsauce.game.gameview

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}

//noinspection SpellCheckingInspection
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

  protected def entityArea(entity: AbilityComponent): AreaId =
    entity.currentAreaId

  protected def createRenderable(
      id: GameEntityId[AbilityComponent]
  )(implicit game: CoreGame): AbilityRenderable = {
    val r = AbilityRenderable(id)
    r.init()
    r
  }
}
