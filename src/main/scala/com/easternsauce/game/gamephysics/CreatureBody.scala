package com.easternsauce.game.gamephysics

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

case class CreatureBody(creatureId: GameEntityId[Creature])
    extends PhysicsBody {

  def init(areaWorld: AreaWorld, pos: Vector2f)(implicit
      game: CoreGame
  ): Unit = {
    this.b2Body = createBody(areaWorld, pos, game)
    this.areaWorld = areaWorld
    this.sensor = false
  }

  private def createBody(
      areaWorld: AreaWorld,
      pos: Vector2f,
      game: CoreGame
  ): Body = {
    val creature = game.gameState.creatures(creatureId)

    import com.badlogic.gdx.physics.box2d._

    val bodyDef = new BodyDef()
    bodyDef.`type` = BodyType.DynamicBody
    bodyDef.position.set(pos.x, pos.y)

    val body = areaWorld.createBody(bodyDef)
    body.setUserData(this)

    val fixtureDef = new FixtureDef()
    val shape = new CircleShape()
    shape.setRadius(creature.params.bodyRadius)
    fixtureDef.shape = shape

    body.createFixture(fixtureDef)

    body.setLinearDamping(10f)

    val massData = new MassData
    massData.mass = 1000f
    body.setMassData(massData)

    body

  }

  override def update(gameState: GameState): Unit = {
    if (gameState.creatures.contains(creatureId)) {
      val creature = gameState.creatures(creatureId)

      b2Body.setLinearVelocity(
        creature.params.velocity.x,
        creature.params.velocity.y
      )
    }
  }

  override def onRemove(): Unit = {
    areaWorld.b2World.destroyBody(b2Body)
  }
}
