package com.easternsauce.game.gameview

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ScreenUtils
import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.id.AreaId

case class GameView() {

  private var cameraSystem: CameraSystem = _
  private var sceneView: SceneView = _
  private var fpsCountRenderer: FpsCountRenderer = _
  private var simulationDebugRenderer: SimulationDebugRenderer = _

  var skin: Skin = _
  var spriteBatchHolder: SpriteBatchHolder = _

  var debugEnabled: Boolean = true

  def init(): Unit = {
    skin = new Skin(Gdx.files.internal(Constants.DefaultSkinPath))

    spriteBatchHolder = SpriteBatchHolder()
    spriteBatchHolder.init()

    cameraSystem = CameraSystem()
    cameraSystem.init()

    sceneView = SceneView()
    sceneView.init(cameraSystem)

    simulationDebugRenderer = SimulationDebugRenderer()
    simulationDebugRenderer.init(cameraSystem)

    fpsCountRenderer = FpsCountRenderer()
    fpsCountRenderer.init()
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

    cameraSystem.setProjectionMatrices(spriteBatchHolder)

    sceneView.render(
      areaId,
      spriteBatchHolder,
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
    spriteBatchHolder.hudSpriteBatch.begin()
    fpsCountRenderer.render(spriteBatchHolder, skin)
    spriteBatchHolder.hudSpriteBatch.end()
  }

  def resize(width: Int, height: Int): Unit = {
    cameraSystem.resize(width, height)
  }
}
