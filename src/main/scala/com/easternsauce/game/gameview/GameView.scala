package com.easternsauce.game.gameview

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ScreenUtils
import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.id.AreaId

case class GameView() {

  private var viewportManager: ViewportManager = _
  private var worldRenderer: WorldRenderer = _

  var skin: Skin = _
  var spriteBatchHolder: SpriteBatchHolder = _

  def init(): Unit = {
    skin = new Skin(Gdx.files.internal(Constants.DefaultSkinPath))

    spriteBatchHolder = SpriteBatchHolder()
    spriteBatchHolder.init()

    viewportManager = ViewportManager()
    viewportManager.init()

    worldRenderer = WorldRenderer()
    worldRenderer.init()
  }

  def update(areaId: AreaId, delta: Float)(implicit
      game: CoreGame
  ): Unit = {
    worldRenderer.update(areaId)

    val creatureId = game.clientCreatureId

    viewportManager.updateCameras(creatureId)
  }

  def render(areaId: AreaId, delta: Float)(implicit
      game: CoreGame
  ): Unit = {
    ScreenUtils.clear(0, 0, 0, 1)

    viewportManager.setProjectionMatrices(spriteBatchHolder)

    worldRenderer.render(
      areaId,
      spriteBatchHolder,
      skin,
      viewportManager.getWorldCameraPos
    )

    if (Constants.EnableDebug) {
      viewportManager.renderDebug(game.gameplay.physics.areaWorlds(areaId))
    }
  }

  def resize(width: Int, height: Int): Unit = {
    viewportManager.resize(width, height)
  }
}
