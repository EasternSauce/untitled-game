package com.easternsauce.game.gamephysics

import com.badlogic.gdx.physics.box2d.Body
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.math.Vector2f

trait PhysicsBody {
  protected var b2Body: Body = _
  protected var areaWorld: AreaWorld = _
  protected var _sensor: Boolean = false

  def init(areaWorld: AreaWorld, pos: Vector2f, gameState: GameState): Unit

  def update(gameState: GameState): Unit

  def sensor: Boolean = _sensor

  def setSensor(): Unit = {
    b2Body.getFixtureList.get(0).setSensor(true)
    _sensor = true
  }

  def setNonSensor(): Unit = {
    b2Body.getFixtureList.get(0).setSensor(false)
    _sensor = false
  }

  def setPos(pos: Vector2f): Unit = {
    b2Body.setTransform(
      pos.x,
      pos.y,
      b2Body.getAngle
    )
  }

  def pos: Vector2f = {
    Vector2f(b2Body.getPosition.x, b2Body.getPosition.y)
  }

  def onRemove(): Unit
}
