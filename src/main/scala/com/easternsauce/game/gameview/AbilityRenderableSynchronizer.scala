package com.easternsauce.game.gameview

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.Ability
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}

import scala.collection.mutable

//noinspection SpellCheckingInspection
case class AbilityRenderableSynchronizer() {
  private var abilityRenderables
      : mutable.Map[GameEntityId[Ability], AbilityRenderable] = _

  def init(
      abilityRenderables: mutable.Map[GameEntityId[
        Ability
      ], AbilityRenderable]
  ): Unit = {
    this.abilityRenderables = abilityRenderables
  }

  def synchronizeForArea(areaId: AreaId)(implicit game: CoreGame): Unit = {
    val abilityRenderablesToCreate =
      (game.gameState.abilities.keys.toSet -- abilityRenderables.keys.toSet)
        .filter(game.gameState.abilities(_).currentAreaId == areaId)
    val abilityRendererablesToDestroy =
      (abilityRenderables.keys.toSet -- game.gameState.abilities.keys.toSet)
        .filter(abilityId =>
          !game.gameState.abilities.contains(abilityId) ||
            game.gameState.abilities(abilityId).currentAreaId == areaId
        )

    abilityRenderablesToCreate.foreach(createAbilityRenderable(_))
    abilityRendererablesToDestroy.foreach(destroyAbilityRenderable(_))
  }

  private def createAbilityRenderable(
      abilityId: GameEntityId[Ability]
  )(implicit game: CoreGame): Unit = {
    val abilityRenderable = AbilityRenderable(abilityId)
    abilityRenderable.init()
    abilityRenderables.update(abilityId, abilityRenderable)
  }

  private def destroyAbilityRenderable(
      abilityId: GameEntityId[Ability]
  )(implicit game: CoreGame): Unit = {
    abilityRenderables.remove(abilityId)
  }
}
