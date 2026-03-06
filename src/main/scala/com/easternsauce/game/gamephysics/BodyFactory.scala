package com.easternsauce.game.gamephysics

import com.badlogic.gdx.physics.box2d._
import com.easternsauce.game.math.Vector2f

class BodyFactory(areaWorld: AreaWorld) {

  private val bodyDef: BodyDef = new BodyDef()
  private var circleShape: Option[CircleShape] = None
  private var polygonShape: Option[PolygonShape] = None
  private val fixtureDef: FixtureDef = new FixtureDef()
  private var userData: Any = _
  private var linearDamping: Float = 0f
  private var mass: Option[Float] = None

  def withType(t: BodyDef.BodyType): BodyFactory = { bodyDef.`type` = t; this }
  def at(pos: Vector2f): BodyFactory = { bodyDef.position.set(pos.x, pos.y); this }
  def withCircle(radius: Float): BodyFactory = {
    val shape = new CircleShape()
    shape.setRadius(radius)
    circleShape = Some(shape)
    this
  }
  def withPolygon(vertices: Array[Float]): BodyFactory = {
    val shape = new PolygonShape()
    shape.set(vertices)
    polygonShape = Some(shape)
    this
  }
  def withBox(width: Float, height: Float): BodyFactory = {
    val shape = new PolygonShape()
    shape.setAsBox(width, height)
    polygonShape = Some(shape)
    this
  }
  def withSensor(s: Boolean): BodyFactory = { fixtureDef.isSensor = s; this }
  def withLinearDamping(d: Float): BodyFactory = { linearDamping = d; this }
  def withMass(m: Float): BodyFactory = { mass = Some(m); this }
  def withUserData(data: Any): BodyFactory = { userData = data; this }

  def build(): Body = {
    val body = areaWorld.createBody(bodyDef)
    body.setUserData(userData)

    if (circleShape.isDefined) fixtureDef.shape = circleShape.get
    else if (polygonShape.isDefined) fixtureDef.shape = polygonShape.get

    body.createFixture(fixtureDef)

    if (linearDamping > 0) body.setLinearDamping(linearDamping)
    mass.foreach { m =>
      val massData = new MassData()
      massData.mass = m
      body.setMassData(massData)
    }

    body
  }
}
