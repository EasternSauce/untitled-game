package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

abstract class PhysicsBody {

  protected var _pos: Vector2f = Vector2f(0f, 0f)
  protected var _velocity: Vector2f = Vector2f(0f, 0f)
  protected var _shape: PhysicsShape = CircleShape(0f)

  protected var areaPhysicsWorld: AreaPhysicsWorld = _

  def pos: Vector2f = _pos
  def velocity: Vector2f = _velocity
  def shape: PhysicsShape = _shape

  def areaId: AreaId = areaPhysicsWorld.areaId

  def setPos(pos: Vector2f): Unit =
    _pos = pos

  def setVelocity(v: Vector2f): Unit =
    _velocity = v

  def isStatic: Boolean = false
  def isSensor: Boolean = false
  def isPushable(implicit game: CoreGame): Boolean = true

  def init(
      areaPhysicsWorld: AreaPhysicsWorld,
      pos: Vector2f,
      velocity: Vector2f,
      shape: PhysicsShape
  ): Unit = {
    this.areaPhysicsWorld = areaPhysicsWorld
    this._pos = pos
    this._velocity = velocity
    this._shape = shape

    areaPhysicsWorld.registerBody(this)
  }

  def update(delta: Float)(implicit game: CoreGame): Unit = {
    val v = _velocity

    if (v.x != 0f || v.y != 0f) {

      val speed = Math.sqrt(v.x * v.x + v.y * v.y).toFloat

      val size = _shape match {
        case CircleShape(r)  => r
        case RectShape(w, h) => Math.min(w, h) * 0.5f
      }

      val steps =
        Math.max(1, Math.ceil((speed * delta) / size).toInt)

      val stepDelta = delta / steps

      for (_ <- 0 until steps) {
        _pos = Vector2f(
          _pos.x + v.x * stepDelta,
          _pos.y + v.y * stepDelta
        )

        areaPhysicsWorld.resolveCollisionsForBody(this)
      }
    }
  }

  def onRemove(): Unit = {
    areaPhysicsWorld.removeBody(this)
  }
}
