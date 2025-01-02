package com.easternsauce.game.math

import java.lang.Math.PI

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

  def len: Float = Math.sqrt(x * x + y * y).toFloat

  def vectorTowards(point: Vector2f): Vector2f = {
    Vector2f(point.x - x, point.y - y)
  }

  def add(vector: Vector2f): Vector2f = Vector2f(x + vector.x, y + vector.y)

  def withLength(length: Float): Vector2f = {
    normalized.multiply(length)
  }

  def setAngleDeg(degrees: Float): Vector2f = setAngleRad(
    degrees * PI.toFloat / 180
  )

  private def rotateRad(radians: Float): Vector2f = {
    val cos = Math.cos(radians).toFloat
    val sin = Math.sin(radians).toFloat
    val newX = this.x * cos - this.y * sin
    val newY = this.x * sin + this.y * cos
    Vector2f(newX, newY)
  }

  private def setAngleRad(radians: Float): Vector2f = {
    val newVec = Vector2f(len, 0f)
    newVec.rotateRad(radians)
  }

  def normalized: Vector2f = {
    if (len != 0) {
      Vector2f(x / len, y / len)
    } else {
      Vector2f(x, y)
    }
  }

  def multiply(value: Float): Vector2f = Vector2f(x * value, y * value)

}
