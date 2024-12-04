package com.easternsauce.game.gamestate.creature.behavior

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.Creature

trait CreatureBehavior {
  def run()(implicit game: CoreGame): Creature => Creature
}
