package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

case class CreatureBody(creatureId: GameEntityId[Creature]) extends PhysicsBody {

  override protected def radius(implicit game: CoreGame): Float =
    game.gameState.creatures(creatureId).params.bodyRadius

  override protected def velocity(gameState: GameState): Option[Vector2f] =
    gameState.creatures.get(creatureId).map(_.params.velocity)

  override protected def linearDamping: Float = 10f

  override protected def mass: Option[Float] = Some(1000f)
}
