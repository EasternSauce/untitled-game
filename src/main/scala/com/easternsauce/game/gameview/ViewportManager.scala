package com.easternsauce.game.gameview

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.easternsauce.game.CoreGame
import com.easternsauce.game.gamephysics.AreaWorld
import com.easternsauce.game.math.{IsometricProjection, Vector2f}

case class ViewportManager() {

  private val worldViewport: GameViewport = GameViewport()
  private val b2DebugViewport: GameViewport = GameViewport()
  private val worldTextViewport: GameViewport = GameViewport()
  private val hudViewport: GameViewport = GameViewport()

  def init(): Unit = {
    worldViewport.init(
      1,
      pos => IsometricProjection.translatePosIsoToScreen(pos)
    )
    b2DebugViewport.init(0.02f, Predef.identity)

    worldTextViewport.init(
      1,
      pos => IsometricProjection.translatePosIsoToScreen(pos)
    )

    hudViewport.init(
      1,
      Predef.identity
    )
  }

  def setProjectionMatrices(spriteBatches: SpriteBatches): Unit = {
    worldViewport.setProjectionMatrix(spriteBatches.worldSpriteBatch)
    worldTextViewport.setProjectionMatrix(spriteBatches.worldTextSpriteBatch)
    hudViewport.setProjectionMatrix(spriteBatches.hudBatch)
  }

  def updateCameras(
      game: CoreGame
  ): Unit = {
    worldViewport.updateCamera(game.gameState)
    b2DebugViewport.updateCamera(game.gameState)
    worldTextViewport.updateCamera(game.gameState)
    hudViewport.updateCamera(game.gameState)
  }

  def resize(width: Int, height: Int): Unit = {
    worldViewport.updateSize(width, height)
    b2DebugViewport.updateSize(width, height)
    worldTextViewport.updateSize(width, height)
    hudViewport.updateSize(width, height)
  }

  def createHudStage(hudBatch: GameSpriteBatch): Stage =
    hudViewport.createStage(hudBatch)

  def renderDebug(areaWorld: AreaWorld): Unit =
    areaWorld.renderDebug(b2DebugViewport)

  def unprojectHudCamera(screenCoords: Vector3): Unit =
    hudViewport.unprojectCamera(screenCoords)

  def getWorldCameraPos: Vector2f = worldViewport.getCameraPos

}
