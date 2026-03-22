package com.easternsauce.game.gamephysics

import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.gamestate.projectile.ProjectileComponent

case class AbilityBody(
    projectileComponentId: GameEntityId[ProjectileComponent]
) extends PhysicsBody {
  override def isSensor: Boolean = true
}
