package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

abstract class PhysicsBody {

  protected var _pos: Vector2f = Vector2f(0f, 0f)
  protected var _velocity: Vector2f = Vector2f(0f, 0f)

  protected var areaWorld: AreaWorld = _
  protected var sensor: Boolean = false

  def pos: Vector2f = _pos

  def areaId: AreaId = areaWorld.areaId

  def isSensor: Boolean = sensor

  def setPos(pos: Vector2f): Unit =
    _pos = pos

  def setSensor(): Unit =
    sensor = true

  def setNonSensor(): Unit =
    sensor = false

  protected def radius(implicit game: CoreGame): Float

  protected def velocity(gameState: GameState): Option[Vector2f]

  protected def initialSensor: Boolean = false

  def init(areaWorld: AreaWorld, pos: Vector2f)(implicit game: CoreGame): Unit = {
    this.areaWorld = areaWorld
    this._pos = pos
    this.sensor = initialSensor

    areaWorld.registerBody(this)
  }

  def update(gameState: GameState, delta: Float): Unit = {
    velocity(gameState).foreach { v =>
      _velocity = v
    }

    _pos = Vector2f(
      _pos.x + _velocity.x * delta,
      _pos.y + _velocity.y * delta
    )
  }

  def onRemove(): Unit = {
    areaWorld.removeBody(this)
  }
}
