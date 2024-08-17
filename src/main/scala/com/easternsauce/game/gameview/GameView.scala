package com.easternsauce.game.gameview

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ScreenUtils
import com.easternsauce.game.CoreGame

case class GameView() {

  var skin: Skin = _
  var spriteBatches: SpriteBatches = _
  var viewportManager: ViewportManager = _

  private var worldRenderer: WorldRenderer = _

  def init(game: CoreGame): Unit = {
    skin = new Skin(Gdx.files.internal("assets/ui/skin/uiskin.json"))

    spriteBatches = SpriteBatches()
    spriteBatches.worldSpriteBatch.init()
    spriteBatches.worldTextSpriteBatch.init()
    spriteBatches.hudBatch.init()

    viewportManager = ViewportManager()
    viewportManager.init()

    worldRenderer = WorldRenderer()
    worldRenderer.init(game)
  }

  def update(delta: Float, game: CoreGame): Unit = {
    worldRenderer.update(game.gameState)

    viewportManager.updateCameras(game)
  }

  def render(delta: Float, game: CoreGame): Unit = {
    ScreenUtils.clear(0, 0, 0, 1)

    viewportManager.setProjectionMatrices(spriteBatches)

    spriteBatches.worldSpriteBatch.begin()
    game.mapRenderer.render(
      spriteBatches.worldSpriteBatch,
      game.gameState.cameraPos,
      game.gameState
    )
    spriteBatches.worldSpriteBatch.end()

    worldRenderer.drawCurrentWorld(
      spriteBatches,
      game.gameState.cameraPos,
      game
    )

  }

  def resize(width: Int, height: Int): Unit = {
    viewportManager.resize(width, height)
  }
}
