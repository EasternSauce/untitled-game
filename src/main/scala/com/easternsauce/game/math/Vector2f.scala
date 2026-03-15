package com.easternsauce.game.math

import java.lang.Math.PI

case class Vector2f(x: Float, y: Float) {

  // --- Operators ---

  def +(v: Vector2f): Vector2f =
    Vector2f(x + v.x, y + v.y)

  def -(v: Vector2f): Vector2f =
    Vector2f(x - v.x, y - v.y)

  def *(value: Float): Vector2f =
    Vector2f(x * value, y * value)

  def /(value: Float): Vector2f =
    Vector2f(x / value, y / value)

  // --- Compatibility with old code ---

  def add(v: Vector2f): Vector2f =
    this + v

  def multiply(value: Float): Vector2f =
    this * value

  // --- Math helpers ---

  def distance(v: Vector2f): Float = {
    val dx = v.x - x
    val dy = v.y - y
    Math.sqrt(dx * dx + dy * dy).toFloat
  }

  def len: Float =
    Math.sqrt(x * x + y * y).toFloat

  // alias so old physics code works
  def length: Float = len

  def normalized: Vector2f = {
    val l = len
    if (l != 0f) Vector2f(x / l, y / l)
    else Vector2f(0f, 0f)
  }

  def withLength(length: Float): Vector2f =
    normalized * length

  def vectorTowards(point: Vector2f): Vector2f =
    point - this

  // --- Angle helpers ---

  def angleDeg: Float = {
    var angle = Math.atan2(y, x).toFloat * 180f / PI.toFloat
    if (angle < 0) angle += 360f
    angle
  }

  def setAngleDeg(degrees: Float): Vector2f =
    setAngleRad(degrees * PI.toFloat / 180f)

  private def rotateRad(radians: Float): Vector2f = {
    val cos = Math.cos(radians).toFloat
    val sin = Math.sin(radians).toFloat
    Vector2f(
      x * cos - y * sin,
      x * sin + y * cos
    )
  }

  private def setAngleRad(radians: Float): Vector2f = {
    val base = Vector2f(len, 0f)
    base.rotateRad(radians)
  }

}
