package com.easternsauce.game.gameview

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

case class WorldRenderer() {

  private var creatureRenderer: CreatureRenderer = _
  private var abilityRenderer: AbilityRenderer = _
  private var viewportManager: CameraSystem = _

  def init(viewportManager: CameraSystem): Unit = {

    this.viewportManager = viewportManager

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
  )(implicit game: CoreGame): Unit = {

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

    val tiledMap =
      game.gameplay.tiledMapsManager.tiledMaps(areaId)

    tiledMap.renderBottomLayers(worldSpriteBatch, worldCameraPos)

    creatureRenderer.renderDeadCreatures(
      areaId,
      worldSpriteBatch,
      worldCameraPos
    )

    renderDynamicAndCreatures(
      areaId,
      tiledMap,
      worldSpriteBatch,
      worldCameraPos
    )

    abilityRenderer.renderAbilities(
      areaId,
      worldSpriteBatch,
      worldCameraPos
    )

    creatureRenderer.renderLifeBars(
      areaId,
      worldSpriteBatch
    )

    tiledMap.renderTopLayers(worldSpriteBatch, worldCameraPos)

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

  private def renderDynamicAndCreatures(
      areaId: AreaId,
      tiledMap: GameTiledMap,
      batch: GameSpriteBatch,
      worldCameraPos: Vector2f
  )(implicit game: CoreGame): Unit = {

    val dynamicLayerRenderables =
      tiledMap.getDynamicLayerCells()

    val aliveCreatureRenderables =
      creatureRenderer.getAliveCreatureRenderables(areaId)

    val width =
      tiledMap.layerWidth("fill")

    val topOfMap = Vector2f(0, width)

    val sortedRenderables =
      (dynamicLayerRenderables ++ aliveCreatureRenderables)
        .sortBy(_.pos())((v1: Vector2f, v2: Vector2f) => {
          val d1 = v1.distance(topOfMap)
          val d2 = v2.distance(topOfMap)

          if (d2 - d1 > 0f) -1
          else if (d2 - d1 < 0f) 1
          else 0
        })

    sortedRenderables.foreach(
      _.render(batch, worldCameraPos)
    )
  }

  def update(areaId: AreaId)(implicit game: CoreGame): Unit = {
    abilityRenderer.synchronizeRenderables(areaId)
    creatureRenderer.synchronizeRenderables(areaId)
  }
}
