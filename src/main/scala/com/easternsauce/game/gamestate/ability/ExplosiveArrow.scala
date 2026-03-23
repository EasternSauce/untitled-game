package com.easternsauce.game.gamestate.ability
import com.easternsauce.game.core.CoreGame

case class ExplosiveArrow(params: AbilityParams) extends Ability {

  override def onActiveStart()(implicit game: CoreGame): Ability = {

    this
  }

  override def channelTime: Float = 0.5f

  override def finishWhenComponentsDestroyed: Boolean = true

  override def copy(params: AbilityParams): Ability =
    ExplosiveArrow(params)
}
