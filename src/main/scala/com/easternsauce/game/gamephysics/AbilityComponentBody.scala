package com.easternsauce.game.gamephysics

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

case class AbilityComponentBody(
    abilityComponentId: GameEntityId[AbilityComponent]
) extends PhysicsBody {

  def init(areaWorld: AreaWorld, pos: Vector2f)(implicit
      game: CoreGame
  ): Unit = {
    this.b2Body = createBody(areaWorld, pos, game)
    this.areaWorld = areaWorld
    this.sensor = true
  }

  private def createBody(
      areaWorld: AreaWorld,
      pos: Vector2f,
      game: CoreGame
  ): Body = {
    val ability = game.gameState.abilityComponents(abilityComponentId)

    import com.badlogic.gdx.physics.box2d._

    val bodyDef = new BodyDef()
    bodyDef.`type` = BodyType.DynamicBody
    bodyDef.position.set(pos.x, pos.y)

    val body = areaWorld.createBody(bodyDef)
    body.setUserData(this)

    val fixtureDef = new FixtureDef()
    val shape = new CircleShape()
    shape.setRadius(ability.bodyRadius)
    fixtureDef.shape = shape

    body.createFixture(fixtureDef)

    body.getFixtureList.get(0).setSensor(true)

    body

  }

  override def update(gameState: GameState): Unit = {
    if (gameState.abilityComponents.contains(abilityComponentId)) {
      val creature = gameState.abilityComponents(abilityComponentId)

      b2Body.setLinearVelocity(
        creature.velocity.x,
        creature.velocity.y
      )
    }
  }

  override def onRemove(): Unit = {
    areaWorld.b2World.destroyBody(b2Body)
  }
}
