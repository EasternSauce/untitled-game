package com.easternsauce.game.math

case class Vector2f(x: Float, y: Float) {
  def distance(v: Vector2f): Float = {
    val x_d = v.x - x
    val y_d = v.y - y
    Math.sqrt(x_d * x_d + y_d * y_d).toFloat
  }

  def angleDeg: Float = {
    var angle = Math.atan2(y, x).toFloat * 180f / 3.141592653589793f
    if (angle < 0) angle += 360
    angle
  }

  def length: Float = Math.sqrt(x * x + y * y).toFloat

}
