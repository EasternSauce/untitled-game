package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

case class AbilityBody(
                        abilityComponentId: GameEntityId[AbilityComponent]
                      ) extends PhysicsBody {

  override protected def radius(implicit game: CoreGame): Float =
    game.gameState.abilityComponents(abilityComponentId).bodyRadius

  override protected def velocity(implicit game: CoreGame): Option[Vector2f] =
    game.gameState.abilityComponents
      .get(abilityComponentId)
      .map(_.velocity)

  override protected def initialSensor: Boolean = true
}
