package com.easternsauce.game.gameview

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.scenes.scene2d.Stage
import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.IsometricProjection
import com.easternsauce.game.math.Vector2f

case class CameraSystem() {

  private val worldViewport: GameViewport = GameViewport()
  private val b2DebugViewport: GameViewport = GameViewport()
  private val worldTextViewport: GameViewport = GameViewport()
  private val hudViewport: GameViewport = GameViewport()

  def init(): Unit = {

    worldViewport.init(
      Constants.ViewportWorldWidth,
      Constants.ViewportWorldHeight,
      1f,
      pos => IsometricProjection.isoToScreenCompensated(pos)
    )

    b2DebugViewport.init(
      Constants.ViewportWorldWidth,
      Constants.ViewportWorldHeight,
      0.02f,
      Predef.identity
    )

    worldTextViewport.init(
      Constants.ViewportWorldWidth,
      Constants.ViewportWorldHeight,
      1f,
      pos => IsometricProjection.isoToScreenCompensated(pos)
    )

    hudViewport.init(
      Constants.WindowWidth.toFloat,
      Constants.WindowHeight.toFloat,
      1f,
      Predef.identity
    )
  }

  def update(
      creatureId: Option[GameEntityId[Creature]]
  )(implicit game: CoreGame): Unit = {

    worldViewport.updateCamera(creatureId)
    b2DebugViewport.updateCamera(creatureId)
    worldTextViewport.updateCamera(creatureId)
  }

  def resize(width: Int, height: Int): Unit = {
    worldViewport.updateSize(width, height)
    b2DebugViewport.updateSize(width, height)
    worldTextViewport.updateSize(width, height)
    hudViewport.updateSize(width, height)
  }

  def setProjectionMatrices(
      worldBatch: RenderBatch,
      worldTextBatch: RenderBatch,
      hudBatch: RenderBatch
  ): Unit = {
    worldViewport.setProjectionMatrix(worldBatch)
    worldTextViewport.setProjectionMatrix(worldTextBatch)
    hudViewport.setProjectionMatrix(hudBatch)
  }

  def createHudStage(batch: RenderBatch): Stage =
    hudViewport.createStage(batch)

  def getWorldCombinedMatrix: Matrix4 =
    worldViewport.getCombinedMatrix

  def getB2DebugCombinedMatrix: Matrix4 =
    b2DebugViewport.getCombinedMatrix

  def getWorldCameraPos: Vector2f =
    worldViewport.getCameraPos
}
