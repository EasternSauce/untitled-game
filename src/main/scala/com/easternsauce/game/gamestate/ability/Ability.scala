package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameEntity

trait Ability extends GameEntity {
  val params: AbilityParams

  def perform()(implicit game: CoreGame): Ability

  def copy(params: AbilityParams): Ability

}
