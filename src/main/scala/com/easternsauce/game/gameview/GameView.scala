package com.easternsauce.game.gameview

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ScreenUtils
import com.easternsauce.game.{Constants, CoreGame}

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

  def update(delta: Float, game: CoreGame): Unit = {
    worldRenderer.update(game.gameState)

    val creatureId = game.clientCreatureId

    viewportManager.updateCameras(creatureId, game)
  }

  def render(delta: Float, game: CoreGame): Unit = {
    ScreenUtils.clear(0, 0, 0, 1)

    viewportManager.setProjectionMatrices(spriteBatchHolder)

    worldRenderer.drawCurrentWorld(
      spriteBatchHolder,
      viewportManager.getWorldCameraPos,
      game
    )

    if (Constants.EnableDebug) {
      game.clientCreatureAreaId.foreach(areaId =>
        viewportManager.renderDebug(game.physics.areaWorlds(areaId))
      )
    }
  }

  def resize(width: Int, height: Int): Unit = {
    viewportManager.resize(width, height)
  }
}
