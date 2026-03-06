package com.easternsauce.game.gamephysics

import com.badlogic.gdx.physics.box2d.BodyDef
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

case class AbilityBody(
    abilityComponentId: GameEntityId[AbilityComponent]
) extends PhysicsBody {

  override protected def radius(implicit game: CoreGame): Float =
    game.gameState.abilityComponents(abilityComponentId).bodyRadius

  override protected def velocity(gameState: GameState): Option[Vector2f] =
    gameState.abilityComponents.get(abilityComponentId).map(_.velocity)

  override protected def initialSensor: Boolean = true

  override def init(areaWorld: AreaWorld, pos: Vector2f)(implicit game: CoreGame): Unit = {
    this.b2Body = new BodyFactory(areaWorld)
      .withType(BodyDef.BodyType.DynamicBody)
      .at(pos)
      .withCircle(radius)
      .withSensor(initialSensor)
      .withUserData(this)
      .build()

    this.areaWorld = areaWorld
    this.sensor = initialSensor
  }
}
