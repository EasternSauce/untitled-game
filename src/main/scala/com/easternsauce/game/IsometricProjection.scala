package com.easternsauce.game

import com.badlogic.gdx.math.{Matrix4, Vector2, Vector3}

object IsometricProjection {
  private val isoTransform: Matrix4 = {
    val matrix: Matrix4 = new Matrix4()

    matrix.idt()

    matrix.scale(
      (Math.sqrt(2.0) / 2.0).toFloat,
      (Math.sqrt(2.0) / 4.0).toFloat,
      1.0f
    )
    matrix.rotate(0.0f, 0.0f, 1.0f, -45)

    matrix
  }
  private val invIsoTransform = {
    new Matrix4(isoTransform).inv
  }

  def translatePosIsoToScreen(pos: Vector2f): Vector2f = {
    val screenPos = new Vector3()
    screenPos.set(pos.x, pos.y, 0)
    screenPos.mul(isoTransform)
    Vector2f(
      screenPos.x * Constants.TileSize * Constants.MapTextureScale,
      screenPos.y * Constants.TileSize * Constants.MapTextureScale
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
