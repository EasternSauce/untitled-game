package com.easternsauce.game.gameview

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable

//noinspection SpellCheckingInspection
case class AbilityRenderer() {
  private var abilityRenderables
      : mutable.Map[GameEntityId[AbilityComponent], AbilityRenderable] = _
  private var abilityRenderableSynchronizer: AbilityRenderableSynchronizer =
    _

  def init(): Unit = {
    abilityRenderables = mutable.Map()

    abilityRenderableSynchronizer = AbilityRenderableSynchronizer()
    abilityRenderableSynchronizer.init(abilityRenderables)
  }

  def renderAbilities(
      areaId: AreaId,
      worldSpriteBatch: GameSpriteBatch,
      worldCameraPos: Vector2f
  )(implicit game: CoreGame): Unit = {
    abilityRenderablesInArea(areaId)
      .foreach(_.render(worldSpriteBatch, worldCameraPos))
  }

  private def abilityRenderablesInArea(areaId: AreaId)(implicit
      game: CoreGame
  ): List[AbilityRenderable] = {
    game.gameState.abilityComponents
      .filter { case (_, ability) =>
        ability.currentAreaId == areaId && abilityRenderables
          .contains(ability.id)
      }
      .keys
      .toList
      .map(abilityId => abilityRenderables(abilityId))
  }

  def synchronizeRenderables(areaId: AreaId)(implicit game: CoreGame): Unit = {
    abilityRenderableSynchronizer.synchronizeForArea(areaId)
  }
}
