package com.easternsauce.game.math

import com.badlogic.gdx.math.{Matrix4, Vector3}
import com.easternsauce.game.Constants

object IsometricProjection {

  // --- Base isometric transform factors ---
  private val ISO_SCALE_X: Float = (Math.sqrt(2.0) / 2.0).toFloat
  private val ISO_SCALE_Y: Float = (Math.sqrt(2.0) / 4.0).toFloat
  private val ISO_ROTATION_DEG: Float = -45f

  // Base isometric transformation matrix
  private val isoTransform: Matrix4 = {
    val m = new Matrix4()
    m.idt()
    m.scale(ISO_SCALE_X, ISO_SCALE_Y, 1f)
    m.rotate(0f, 0f, 1f, ISO_ROTATION_DEG)
    m
  }

  private val invIsoTransform: Matrix4 = new Matrix4(isoTransform).inv()

  // --- Visual compensation for perceived movement ---
  private val visualScaleX: Float = 0.85f
  private val visualScaleY: Float = 1.0f

  // Converts world isometric position to screen coordinates (raw, unadjusted)
  private def isoToScreen(pos: Vector2f): Vector2f = {
    val temp = new Vector3(pos.x, pos.y, 0)
    temp.mul(isoTransform)
    screenScale(temp.x, temp.y)
  }

  // Apply tile size and map texture scaling
  private def screenScale(x: Float, y: Float): Vector2f =
    Vector2f(x * Constants.TileSize * Constants.MapTextureScale,
      y * Constants.TileSize * Constants.MapTextureScale)

  // Converts world iso coordinates to screen coordinates with visual adjustment
  def isoToScreenAdjusted(pos: Vector2f): Vector2f = {
    val base = isoToScreen(pos)
    Vector2f(base.x * visualScaleX, base.y * visualScaleY)
  }

  // Converts screen coordinates back to world iso coordinates
  def screenToIso(pos: Vector2f): Vector2f = {
    val temp = new Vector3(
      pos.x / (Constants.TileSize * Constants.MapTextureScale),
      pos.y / (Constants.TileSize * Constants.MapTextureScale),
      0
    )
    temp.mul(invIsoTransform)
    Vector2f(temp.x, temp.y)
  }
}