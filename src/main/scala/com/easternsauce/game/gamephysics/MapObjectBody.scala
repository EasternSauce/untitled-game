package com.easternsauce.game.gamephysics

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.{BodyDef, FixtureDef, PolygonShape}
import com.easternsauce.game.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.math.Vector2f

case class MapObjectBody(objectBodyId: String) extends PhysicsBody {
  def init(areaWorld: AreaWorld, pos: Vector2f)(implicit
      game: CoreGame
  ): Unit = {
    val bodyDef = new BodyDef()
    bodyDef.`type` = BodyType.StaticBody
    bodyDef.position.set(pos.x + 0.5f, pos.y + 0.5f)

    val body = areaWorld.createBody(bodyDef)
    body.setUserData(this)

    val fixtureDef = new FixtureDef()

    val shape = new PolygonShape()

    shape.set(Array(-0.5f, -0.25f, 0.5f, -0.75f, 0.5f, 0.25f, -0.5f, 0.75f))

    fixtureDef.shape = shape

    body.createFixture(fixtureDef)

    this.b2Body = body

    this.areaWorld = areaWorld
    this.sensor = false
  }

  override def update(gameState: GameState): Unit = {}

  override def onRemove(): Unit = {}
}
