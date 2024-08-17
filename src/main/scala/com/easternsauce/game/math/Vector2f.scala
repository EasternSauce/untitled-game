package com.easternsauce.game.math

case class Vector2f(x: Float, y: Float) {
  def distance(v: Vector2f): Float = {
    val x_d = v.x - x
    val y_d = v.y - y
    Math.sqrt(x_d * x_d + y_d * y_d).toFloat
  }
}
