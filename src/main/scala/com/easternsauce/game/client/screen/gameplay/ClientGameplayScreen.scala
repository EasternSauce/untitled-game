package com.easternsauce.game.client.screen.gameplay

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMapRenderer
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.{Gdx, Screen}
import com.easternsauce.game.{CoreGame, Vector2f}

case class ClientGameplayScreen(game: CoreGame) extends Screen {
  private val inputProcessor: ClientGameplayInputProcessor =
    ClientGameplayInputProcessor(game)

  override def show(): Unit = {
    Gdx.input.setInputProcessor(inputProcessor)
  }

  override def render(delta: Float): Unit = {
    game.viewportManager.updateCameras(game)

    ScreenUtils.clear(0, 0, 0, 1)

    game.viewportManager.setProjectionMatrices(game.spriteBatches)


    game.spriteBatches.worldSpriteBatch.begin()

    game.mapRenderer.render(game.spriteBatches.worldSpriteBatch, game.gameState.cameraPos, game.gameState)

    game.spriteBatches.worldSpriteBatch.end()

    println(game.gameState.cameraPos)
  }

  override def resize(width: Int, height: Int): Unit = {
    game.viewportManager.resize(width, height)
  }

  override def pause(): Unit = {}

  override def resume(): Unit = {}

  override def hide(): Unit = {}

  override def dispose(): Unit = {}
}
