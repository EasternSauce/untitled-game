package com.easternsauce.game.gameview

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamemap.GameMapCell
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

case class SceneView() {

  private var terrainRenderer: TerrainRenderer = _
  private var creatureRenderer: CreatureRenderController = _
  private var abilityRenderer: AbilityRenderController = _
  private var cameraSystem: CameraSystem = _

  def init(cameraSystem: CameraSystem): Unit = {
    this.cameraSystem = cameraSystem

    terrainRenderer = TerrainRenderer()

    creatureRenderer = CreatureRenderController()
    creatureRenderer.init()

    abilityRenderer = AbilityRenderController()
    abilityRenderer.init()
  }

  def render(
      areaId: AreaId,
      batch: RenderBatch,
      skin: Skin,
      cameraPos: Vector2f
  )(implicit game: CoreGame): Unit = {

    val tiledMap = game.gameplay.tiledMapsManager.tiledMaps(areaId)

    renderWorld(areaId, tiledMap, batch, cameraPos)
  }

  private def renderWorld(
      areaId: AreaId,
      tiledMap: GameTiledMap,
      batch: RenderBatch,
      cameraPos: Vector2f
  )(implicit game: CoreGame): Unit = {

    batch.begin()

    renderTerrainBottom(areaId, tiledMap, batch, cameraPos)
    renderDeadCreatures(areaId, batch, cameraPos)
    renderDynamicAndAlive(areaId, tiledMap, batch, cameraPos)
    renderAbilities(areaId, batch, cameraPos)
    renderLifeBars(areaId, batch)
    renderTerrainTop(areaId, tiledMap, batch, cameraPos)

    batch.end()
  }

  // --- Rendering steps ---

  private def renderTerrainBottom(
      areaId: AreaId,
      tiledMap: GameTiledMap,
      batch: RenderBatch,
      cameraPos: Vector2f
  )(implicit game: CoreGame): Unit =
    terrainRenderer.renderBottomTerrainLayer(areaId, tiledMap, batch, cameraPos)

  private def renderTerrainTop(
      areaId: AreaId,
      tiledMap: GameTiledMap,
      batch: RenderBatch,
      cameraPos: Vector2f
  )(implicit game: CoreGame): Unit =
    terrainRenderer.renderTopTerrainLayer(areaId, tiledMap, batch, cameraPos)

  private def renderDeadCreatures(
      areaId: AreaId,
      batch: RenderBatch,
      cameraPos: Vector2f
  )(implicit game: CoreGame): Unit =
    creatureRenderer.renderDeadCreatures(areaId, batch, cameraPos)

  private def renderAbilities(
      areaId: AreaId,
      batch: RenderBatch,
      cameraPos: Vector2f
  )(implicit game: CoreGame): Unit =
    abilityRenderer.renderAbilities(areaId, batch, cameraPos)

  private def renderLifeBars(
      areaId: AreaId,
      batch: RenderBatch
  )(implicit game: CoreGame): Unit =
    creatureRenderer.renderLifeBars(areaId, batch)

  // --- Dynamic creatures (sorted) ---

  private def renderDynamicAndAlive(
      areaId: AreaId,
      tiledMap: GameTiledMap,
      batch: RenderBatch,
      worldCameraPos: Vector2f
  )(implicit game: CoreGame): Unit = {

    val dynamicLayerRenderables =
      tiledMap.getDynamicLayerCells()

    val aliveCreatureRenderables =
      creatureRenderer.getAliveCreatureRenderables(areaId)

    val sortedRenderables =
      (dynamicLayerRenderables ++ aliveCreatureRenderables)
        .sortBy { r =>
          val bias = if (r.isInstanceOf[GameMapCell]) 0f else 0.5f
          -(r.pos().y - r.pos().x) + bias
        }
    sortedRenderables.foreach(
      _.render(batch, worldCameraPos)
    )
  }

  // --- Update ---

  def update(areaId: AreaId)(implicit game: CoreGame): Unit = {
    creatureRenderer.synchronizeRenderables(areaId)
    abilityRenderer.synchronizeRenderables(areaId)
  }
}
