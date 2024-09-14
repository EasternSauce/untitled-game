package com.easternsauce.game.gameview

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamephysics.AreaWorld
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId
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

  def setProjectionMatrices(spriteBatchHolder: SpriteBatchHolder): Unit = {
    worldViewport.setProjectionMatrix(spriteBatchHolder.worldSpriteBatch)
    worldTextViewport.setProjectionMatrix(
      spriteBatchHolder.worldTextSpriteBatch
    )
    hudViewport.setProjectionMatrix(spriteBatchHolder.hudBatch)
  }

  def updateCameras(
      creatureId: Option[GameEntityId[Creature]]
  )(implicit game: CoreGame): Unit = {
    worldViewport.updateCamera(creatureId)
    b2DebugViewport.updateCamera(creatureId)
    worldTextViewport.updateCamera(creatureId)
    hudViewport.updateCamera(creatureId)
  }

  def resize(width: Int, height: Int): Unit = {
    worldViewport.updateSize(width, height)
    b2DebugViewport.updateSize(width, height)
    worldTextViewport.updateSize(width, height)
    hudViewport.updateSize(width, height)
  }

  def createHudStage(hudBatch: GameSpriteBatch): Stage = {
    hudViewport.createStage(hudBatch)
  }

  def renderDebug(areaWorld: AreaWorld): Unit = {
    areaWorld.renderDebug(b2DebugViewport)
  }

  def unprojectHudCamera(screenCoords: Vector3): Unit = {
    hudViewport.unprojectCamera(screenCoords)
  }

  def getWorldCameraPos: Vector2f = worldViewport.getCameraPos

}
