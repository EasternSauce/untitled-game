package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

case class CreatureBody(creatureId: GameEntityId[Creature]) extends PhysicsBody {

  override def radius(implicit game: CoreGame): Float =
    game.gameState.creatures(creatureId).params.bodyRadius

  override def velocity(implicit game: CoreGame): Option[Vector2f] =
    game.gameState.creatures.get(creatureId).map(_.params.velocity)

  override def isPushable(implicit game: CoreGame): Boolean =
    !game.gameState.creatures(creatureId).params.isPlayer
}
