package com.easternsauce.game.gameview

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

case class WorldRenderFlow() {

  private var terrainRenderer: TerrainRenderer = _
  private var creatureRenderer: CreatureRenderer = _
  private var abilityRenderer: AbilityRenderer = _
  private var viewportManager: CameraSystem = _

  def init(viewportManager: CameraSystem): Unit = {

    this.viewportManager = viewportManager

    terrainRenderer = TerrainRenderer()

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

    val tiledMap =
      game.gameplay.tiledMapsManager.tiledMaps(areaId)

    renderWorld(
      areaId,
      tiledMap,
      spriteBatchHolder.worldSpriteBatch,
      worldCameraPos,
      skin
    )
  }

  private def renderWorld(
      areaId: AreaId,
      tiledMap: GameTiledMap,
      worldSpriteBatch: GameSpriteBatch,
      worldCameraPos: Vector2f,
      skin: Skin
  )(implicit game: CoreGame): Unit = {

    worldSpriteBatch.begin()

    terrainRenderer.renderBottomTerrainLayer(
      areaId,
      tiledMap,
      worldSpriteBatch,
      worldCameraPos
    )

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

    terrainRenderer.renderTopTerrainLayer(
      areaId,
      tiledMap,
      worldSpriteBatch,
      worldCameraPos
    )

    worldSpriteBatch.end()
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
        .sortBy(_.pos())((v1, v2) => {
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
    creatureRenderer.synchronizeRenderables(areaId)
    abilityRenderer.synchronizeRenderables(areaId)
  }
}
