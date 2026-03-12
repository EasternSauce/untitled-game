package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.math.Vector2f

case class StaticObjectBody(objectBodyId: String) extends PhysicsBody {

  override protected def radius(implicit game: CoreGame): Float = 0.5f

  override protected def velocity(gameState: GameState): Option[Vector2f] = None

}
