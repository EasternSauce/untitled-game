package com.easternsauce.game.gameview

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

case class WorldRenderer() {
  private var creatureRenderer: CreatureRenderer = _
  private var abilityRenderer: AbilityRenderer = _

  def init(): Unit = {
    creatureRenderer = CreatureRenderer()
    creatureRenderer.init()
    abilityRenderer = AbilityRenderer()
    abilityRenderer.init()
  }

  def renderForArea(
      areaId: AreaId,
      spriteBatchHolder: SpriteBatchHolder,
      worldCameraPos: Vector2f
  )(implicit
      game: CoreGame
  ): Unit = {
    spriteBatchHolder.worldSpriteBatch.begin()

    renderWorldElementsInAreaByPriority(
      areaId,
      spriteBatchHolder.worldSpriteBatch,
      worldCameraPos
    )

    spriteBatchHolder.worldSpriteBatch.end()

    spriteBatchHolder.worldTextSpriteBatch.begin()

    spriteBatchHolder.worldTextSpriteBatch.end()
  }

  private def renderWorldElementsInAreaByPriority(
      areaId: AreaId,
      worldSpriteBatch: GameSpriteBatch,
      worldCameraPos: Vector2f
  )(implicit game: CoreGame): Unit = {

    game.gameplay.tiledMaps(areaId).render(worldSpriteBatch, worldCameraPos)

    renderDynamicElementsForArea(areaId, worldSpriteBatch, worldCameraPos)
  }

  private def renderDynamicElementsForArea(
      areaId: AreaId,
      worldSpriteBatch: GameSpriteBatch,
      worldCameraPos: Vector2f
  )(implicit game: CoreGame): Unit = {
    abilityRenderer.renderAbilitiesForArea(
      areaId,
      worldSpriteBatch,
      worldCameraPos
    )

    creatureRenderer.renderAliveCreaturesForArea(
      areaId,
      worldSpriteBatch,
      worldCameraPos
    )
  }

  def update(areaId: AreaId)(implicit game: CoreGame): Unit = {
    abilityRenderer.synchronizeRenderables(areaId)
    creatureRenderer.synchronizeRenderables(areaId)
  }
}
