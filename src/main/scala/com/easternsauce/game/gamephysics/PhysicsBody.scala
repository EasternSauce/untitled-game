package com.easternsauce.game.gamephysics

import com.badlogic.gdx.physics.box2d.{Body, BodyDef}
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

abstract class PhysicsBody {

  protected var b2Body: Body = _
  protected var areaWorld: AreaWorld = _
  protected var sensor: Boolean = false

  def pos: Vector2f = {
    val p = b2Body.getPosition
    Vector2f(p.x, p.y)
  }

  def areaId: AreaId = areaWorld.areaId
  def isSensor: Boolean = sensor

  def setPos(pos: Vector2f): Unit = {
    b2Body.setTransform(pos.x, pos.y, b2Body.getAngle)
  }

  def setSensor(): Unit = {
    sensor = true
    val fixtures = b2Body.getFixtureList
    var i = 0
    while (i < fixtures.size) {
      fixtures.get(i).setSensor(true)
      i += 1
    }
  }

  def setNonSensor(): Unit = {
    sensor = false
    val fixtures = b2Body.getFixtureList
    var i = 0
    while (i < fixtures.size) {
      fixtures.get(i).setSensor(false)
      i += 1
    }
  }

  // Subclasses define their parameters
  protected def radius(implicit game: CoreGame): Float
  protected def velocity(gameState: GameState): Option[Vector2f]
  protected def initialSensor: Boolean = false
  protected def bodyType: BodyDef.BodyType = BodyDef.BodyType.DynamicBody
  protected def linearDamping: Float = 0f
  protected def mass: Option[Float] = None

  // Generic init using BodyFactory
  def init(areaWorld: AreaWorld, pos: Vector2f)(implicit game: CoreGame): Unit = {
    this.b2Body = new BodyFactory(areaWorld)
      .withType(bodyType)
      .at(pos)
      .withCircle(radius)
      .withSensor(initialSensor)
      .withLinearDamping(linearDamping)
      .withMass(mass.getOrElse(0f))
      .withUserData(this)
      .build()

    this.areaWorld = areaWorld
    this.sensor = initialSensor
  }

  def update(gameState: GameState): Unit = {
    velocity(gameState).foreach { v =>
      b2Body.setLinearVelocity(v.x, v.y)
    }
  }

  def onRemove(): Unit = {
    areaWorld.b2World.destroyBody(b2Body)
  }
}
