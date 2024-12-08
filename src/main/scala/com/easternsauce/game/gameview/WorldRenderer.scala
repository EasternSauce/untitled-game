package com.easternsauce.game.gameview

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

//noinspection SpellCheckingInspection
case class WorldRenderer() {
  private var creatureRenderer: CreatureRenderer = _
  private var abilityRenderer: AbilityRenderer = _

  def init(): Unit = {
    creatureRenderer = CreatureRenderer()
    creatureRenderer.init()
    abilityRenderer = AbilityRenderer()
    abilityRenderer.init()
  }

  def render(
      areaId: AreaId,
      spriteBatchHolder: SpriteBatchHolder,
      skin: Skin,
      worldCameraPos: Vector2f
  )(implicit
      game: CoreGame
  ): Unit = {
    renderWorld(
      areaId,
      spriteBatchHolder.worldSpriteBatch,
      worldCameraPos
    )

    renderWorldText(
      areaId,
      spriteBatchHolder.worldTextSpriteBatch,
      skin
    )
  }

  private def renderWorld(
      areaId: AreaId,
      worldSpriteBatch: GameSpriteBatch,
      worldCameraPos: Vector2f
  )(implicit game: CoreGame): Unit = {
    worldSpriteBatch.begin()

    game.gameplay.tiledMapsManager
      .tiledMaps(areaId)
      .renderBottomLayers(worldSpriteBatch, worldCameraPos)

    creatureRenderer.renderDeadCreatures(
      areaId,
      worldSpriteBatch,
      worldCameraPos
    )

    val dynamicLayerRenderables: List[Renderable] =
      game.gameplay.tiledMapsManager.tiledMaps(areaId).getDynamicLayerCells()
    val aliveCreatureRenderables: List[Renderable] =
      creatureRenderer.getAliveCreatureRenderables(areaId)

    val width =
      game.gameplay.tiledMapsManager.tiledMaps(areaId).layerWidth("fill")

    val topOfMap = Vector2f(0, width)

    val sortedRenderables =
      (dynamicLayerRenderables ++ aliveCreatureRenderables).sortBy(_.pos())(
        (v1: Vector2f, v2: Vector2f) => {
          if (v2.distance(topOfMap) - v1.distance(topOfMap) > 0f) {
            -1
          } else if (v2.distance(topOfMap) - v1.distance(topOfMap) < 0f) {
            1
          } else {
            0
          }
        }
      )

    sortedRenderables.foreach(_.render(worldSpriteBatch, worldCameraPos))

    abilityRenderer.renderAbilities(
      areaId,
      worldSpriteBatch,
      worldCameraPos
    )

    creatureRenderer.renderLifeBars(
      areaId,
      worldSpriteBatch
    )

    game.gameplay.tiledMapsManager
      .tiledMaps(areaId)
      .renderTopLayers(worldSpriteBatch, worldCameraPos)

    worldSpriteBatch.end()
  }

  private def renderWorldText(
      areaId: AreaId,
      worldTextSpriteBatch: GameSpriteBatch,
      skin: Skin
  )(implicit game: CoreGame): Unit = {
    worldTextSpriteBatch.begin()

    creatureRenderer.renderPlayerNames(
      areaId,
      worldTextSpriteBatch,
      skin
    )

    worldTextSpriteBatch.end()
  }

  def update(areaId: AreaId)(implicit game: CoreGame): Unit = {
    abilityRenderer.synchronizeRenderables(areaId)
    creatureRenderer.synchronizeRenderables(areaId)
  }
}
