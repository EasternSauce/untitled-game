package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.math.Vector2f

/** Base class for all immovable physics bodies:
  *   - terrain
  *   - static map objects (trees, props, walls, etc.)
  */
case class StaticBody(staticId: String) extends PhysicsBody {

  override protected def radius(implicit game: CoreGame): Float =
    0.5f // temporary until shape system is introduced

  override protected def velocity(implicit game: CoreGame): Option[Vector2f] =
    None

  override protected def initialSensor: Boolean =
    false
}
