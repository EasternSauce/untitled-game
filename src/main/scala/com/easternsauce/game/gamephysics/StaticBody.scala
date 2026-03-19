package com.easternsauce.game.gamephysics

/** Base class for all immovable physics bodies:
  *   - terrain
  *   - static map objects (trees, props, walls, etc.)
  */
case class StaticBody(staticId: String) extends PhysicsBody {
  override def isStatic: Boolean = true
}
