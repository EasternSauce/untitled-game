package com.easternsauce.game.gameview

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ScreenUtils
import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

case class GameView() {

  private var cameraSystem: CameraSystem = _
  private var sceneView: SceneView = _
  private var simulationDebugRenderer: SimulationDebugRenderer = _

  var skin: Skin = _

  var worldRenderBatch: RenderBatch = RenderBatch()
  var worldTextRenderBatch: RenderBatch = RenderBatch()
  var hudRenderBatch: RenderBatch = RenderBatch()

  var debugEnabled: Boolean = true

  def init(): Unit = {
    worldRenderBatch.init()
    worldTextRenderBatch.init()
    hudRenderBatch.init()

    skin = new Skin(Gdx.files.internal(Constants.DefaultSkinPath))

    cameraSystem = CameraSystem()
    cameraSystem.init()

    sceneView = SceneView()
    sceneView.init(cameraSystem)

    simulationDebugRenderer = SimulationDebugRenderer()
    simulationDebugRenderer.init(cameraSystem)
  }

  def update(areaId: AreaId, delta: Float)(implicit
      game: CoreGame
  ): Unit = {
    sceneView.update(areaId)

    val creatureId = game.clientCreatureId
    cameraSystem.update(creatureId)
  }

  def render(areaId: AreaId, delta: Float)(implicit
      game: CoreGame
  ): Unit = {
    ScreenUtils.clear(0, 0, 0, 1)

    cameraSystem.setProjectionMatrices(
      worldRenderBatch,
      worldTextRenderBatch,
      hudRenderBatch
    )

    sceneView.render(
      areaId,
      worldRenderBatch,
      skin,
      cameraSystem.getWorldCameraPos
    )

    if (debugEnabled) {
      simulationDebugRenderer.render(
        areaId,
        game.gameplay.worldSimulation
      )
    }

    renderHud()
  }

  private def renderHud(): Unit = {
    renderFpsCount()
  }

  private def renderFpsCount(): Unit = {
    hudRenderBatch.begin()
    val fps = Gdx.graphics.getFramesPerSecond
    val font = skin.getFont("default-font")
    hudRenderBatch.drawFont(
      font,
      fps.toString + " fps",
      Vector2f(Constants.WindowWidth / 2f - 50f, Constants.WindowHeight / 2f),
      Color.ORANGE
    )
    hudRenderBatch.end()
  }

  def resize(width: Int, height: Int): Unit = {
    cameraSystem.resize(width, height)
  }
}
