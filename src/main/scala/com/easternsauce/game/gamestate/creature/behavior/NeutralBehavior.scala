package com.easternsauce.game.gamestate.creature.behavior

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.Creature

case class NeutralBehavior() extends CreatureBehavior {
  override def run()(implicit game: CoreGame): Creature => Creature =
    creature => creature
}
