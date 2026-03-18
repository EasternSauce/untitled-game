package com.easternsauce.game.gameview

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable

case class AbilityRenderController() {

  private val abilityRenderables =
    mutable.Map[GameEntityId[AbilityComponent], AbilityRenderable]()

  def init(): Unit =
    abilityRenderables.clear()

  def synchronizeRenderables(areaId: AreaId)(implicit
      game: CoreGame
  ): Unit =
    synchronize(areaId)

  def synchronize(areaId: AreaId)(implicit game: CoreGame): Unit = {

    val currentIds =
      game.gameState.abilityComponents.values
        .filter(_.currentAreaId == areaId)
        .map(_.id)
        .toSet

    currentIds.foreach { id =>
      if (!abilityRenderables.contains(id)) {
        val r = AbilityRenderable(id)
        r.init()
        abilityRenderables(id) = r
      }
    }

    abilityRenderables.keys
      .filterNot(currentIds.contains)
      .foreach(abilityRenderables.remove)
  }

  def renderAbilities(
      areaId: AreaId,
      worldBatch: RenderBatch,
      worldCameraPos: Vector2f
  )(implicit game: CoreGame): Unit =
    abilityRenderables
      .collect {
        case (id, renderable) if game.gameState.abilityComponents(id).currentAreaId == areaId =>
          renderable
      }
      .foreach(_.render(worldBatch, worldCameraPos))
}
