package com.easternsauce.game.gameview

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.{Box2DDebugRenderer, World}
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.easternsauce.game.Constants
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

case class GameViewport() {

  private var camera: OrthographicCamera = _
  private var viewport: FitViewport = _
  private var coordinateTransformation: Vector2f => Vector2f = _

  import com.badlogic.gdx.graphics.OrthographicCamera

  def init(
      zoom: Float,
      coordinateTransformation: Vector2f => Vector2f
  ): Unit = {
    camera = new OrthographicCamera()
    camera.zoom = zoom

    this.coordinateTransformation = coordinateTransformation

    viewport = new FitViewport(
      Constants.ViewportWorldWidth,
      Constants.ViewportWorldHeight,
      camera
    )
  }

  def setProjectionMatrix(batch: GameSpriteBatch): Unit = {
    batch.setProjectionMatrix(camera.combined)
  }

  def updateCamera(
      creatureId: Option[GameEntityId[Creature]],
      gameState: GameState
  ): Unit = {
    val camPosition = camera.position

    val cameraPos = creatureId
      .filter(gameState.creatures.contains)
      .map(gameState.creatures(_).pos)
      .getOrElse(Vector2f(0, 0))

    val pos = coordinateTransformation(cameraPos)

    camPosition.x = (math.floor(pos.x * 100) / 100).toFloat
    camPosition.y = (math.floor(pos.y * 100) / 100).toFloat

    camera.update()
  }

  def updateSize(width: Int, height: Int): Unit = {
    viewport.update(width, height)
  }

  def renderB2Debug(debugRenderer: Box2DDebugRenderer, b2World: World): Unit = {
    debugRenderer.render(b2World, camera.combined)
  }

  def getCameraPos: Vector2f = {
    Vector2f(camera.position.x, camera.position.y)
  }

  def createStage(batch: GameSpriteBatch): Stage =
    new Stage(viewport, batch.underlyingSpriteBatch)

  def unprojectCamera(screenCoords: Vector3): Unit = {
    camera.unproject(screenCoords)
  }
}
