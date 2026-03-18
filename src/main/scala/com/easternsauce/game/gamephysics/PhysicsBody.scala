package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

abstract class PhysicsBody {

  protected var _pos: Vector2f = Vector2f(0f, 0f)

  protected var areaWorld: AreaWorld = _

  def pos: Vector2f = _pos

  def areaId: AreaId = areaWorld.areaId

  def setPos(pos: Vector2f): Unit =
    _pos = pos

  def radius(implicit game: CoreGame): Float

  def velocity(implicit game: CoreGame): Option[Vector2f]

  def isStatic: Boolean = false

  def isPushable(implicit game: CoreGame): Boolean = true

  def init(areaWorld: AreaWorld, pos: Vector2f): Unit = {
    this.areaWorld = areaWorld
    this._pos = pos
    areaWorld.registerBody(this)
  }

  def update(delta: Float)(implicit game: CoreGame): Unit = {
    velocity.foreach { v =>
      // 🔑 number of substeps (prevents 1-frame overlap)
      val speed = Math.sqrt(v.x * v.x + v.y * v.y).toFloat
      val steps =
        Math.max(1, Math.ceil((speed * delta) / radius).toInt)

      val stepDelta = delta / steps

      for (_ <- 0 until steps) {
        _pos = Vector2f(
          _pos.x + v.x * stepDelta,
          _pos.y + v.y * stepDelta
        )

        // resolve immediately after each micro-move
        areaWorld.resolveCollisionsForBody(this)
      }
    }
  }

  def onRemove(): Unit = {
    areaWorld.removeBody(this)
  }
}
