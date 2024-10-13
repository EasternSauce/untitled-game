package com.easternsauce.game.gamestate.ability
import com.easternsauce.game.core.CoreGame

case class Arrow(params: AbilityParams) extends Ability {

  override def perform()(implicit game: CoreGame): Ability = {
    this
  }

  override def copy(params: AbilityParams): Ability = Arrow(params)

}
