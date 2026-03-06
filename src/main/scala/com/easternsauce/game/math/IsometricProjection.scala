package com.easternsauce.game.math

import com.badlogic.gdx.math.{Matrix4, Vector3}
import com.easternsauce.game.Constants

object IsometricProjection {

  // Original transformation
  private val isoTransform: Matrix4 = {
    val matrix: Matrix4 = new Matrix4()
    matrix.idt()
    matrix.scale((Math.sqrt(2.0) / 2.0).toFloat, (Math.sqrt(2.0) / 4.0).toFloat, 1.0f)
    matrix.rotate(0.0f, 0.0f, 1.0f, -45)
    matrix
  }

  private val invIsoTransform = new Matrix4(isoTransform).inv

  // --- Visual compensation parameters ---
  // tweak these to adjust perceived movement speed
  var visualScaleX: Float = 0.85f
  var visualScaleY: Float = 1.0f

  /** Converts world iso coordinates to screen coordinates for physics/render consistency */
  private def translatePosIsoToScreen(pos: Vector2f): Vector2f = {
    val screenPos = new Vector3()
    screenPos.set(pos.x, pos.y, 0)
    screenPos.mul(isoTransform)
    Vector2f(
      screenPos.x * Constants.TileSize * Constants.MapTextureScale,
      screenPos.y * Constants.TileSize * Constants.MapTextureScale
    )
  }

  /** Converts world iso coordinates to screen coordinates with visual compensation */
  def translatePosIsoToScreenAdjusted(pos: Vector2f): Vector2f = {
    val baseScreenPos = translatePosIsoToScreen(pos)
    // apply tunable visual scaling
    Vector2f(
      baseScreenPos.x * visualScaleX,
      baseScreenPos.y * visualScaleY
    )
  }

  def translatePosScreenToIso(pos: Vector2f): Vector2f = {
    val screenPos = new Vector3()
    screenPos.set(
      pos.x / (Constants.TileSize * Constants.MapTextureScale),
      pos.y / (Constants.TileSize * Constants.MapTextureScale),
      0
    )
    screenPos.mul(invIsoTransform)
    Vector2f(screenPos.x, screenPos.y)
  }
}
