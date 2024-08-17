package com.easternsauce.game

object Constants {
  val WindowWidth = 1360
  val WindowHeight = 720

  val ViewportWorldWidth = 1650f
  val ViewportWorldHeight = 864f

  val TileSize = 64

  val MapTextureScale = 1f

  val TileCenterX = 0
  val TileCenterY = 0

  val LayersByRenderingOrder: List[String] = List(
    "fill",
    "background",
    "object",
    "manual_object_bottom",
    "manual_object_top"
  )
}
