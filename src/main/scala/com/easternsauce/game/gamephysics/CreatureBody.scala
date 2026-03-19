package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

case class CreatureBody(
                         creatureId: GameEntityId[Creature],
                         isPlayer: Boolean
                       ) extends PhysicsBody {

  override def isPushable(implicit game: CoreGame): Boolean =
    !isPlayer
}
