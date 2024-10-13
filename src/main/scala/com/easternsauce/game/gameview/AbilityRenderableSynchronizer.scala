package com.easternsauce.game.gameview

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}

import scala.collection.mutable

//noinspection SpellCheckingInspection
case class AbilityRenderableSynchronizer() {
  private var abilityRenderables
      : mutable.Map[GameEntityId[AbilityComponent], AbilityRenderable] = _

  def init(
      abilityRenderables: mutable.Map[GameEntityId[
        AbilityComponent
      ], AbilityRenderable]
  ): Unit = {
    this.abilityRenderables = abilityRenderables
  }

  def synchronizeForArea(areaId: AreaId)(implicit game: CoreGame): Unit = {
    val abilityRenderablesToCreate =
      (game.gameState.abilityComponents.keys.toSet -- abilityRenderables.keys.toSet)
        .filter(game.gameState.abilityComponents(_).currentAreaId == areaId)
    val abilityRendererablesToDestroy =
      (abilityRenderables.keys.toSet -- game.gameState.abilityComponents.keys.toSet)
        .filter(abilityId =>
          !game.gameState.abilityComponents.contains(abilityId) ||
            game.gameState.abilityComponents(abilityId).currentAreaId == areaId
        )

    abilityRenderablesToCreate.foreach(createAbilityRenderable(_))
    abilityRendererablesToDestroy.foreach(destroyAbilityRenderable(_))
  }

  private def createAbilityRenderable(
      abilityId: GameEntityId[AbilityComponent]
  )(implicit game: CoreGame): Unit = {
    val abilityRenderable = AbilityRenderable(abilityId)
    abilityRenderable.init()
    abilityRenderables.update(abilityId, abilityRenderable)
  }

  private def destroyAbilityRenderable(
      abilityId: GameEntityId[AbilityComponent]
  )(implicit game: CoreGame): Unit = {
    abilityRenderables.remove(abilityId)
  }
}
