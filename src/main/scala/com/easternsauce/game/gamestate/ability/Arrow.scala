package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.gamestate.creature.FramesDefinition

case class Arrow(params: AbilityParams) extends Ability {
  override val textureFileName: String = "arrow"

  override val worldWidth: Int = 48
  override val worldHeight: Int = 48
  override val textureWidth: Int = 64
  override val textureHeight: Int = 64
  override val framesDefinition: FramesDefinition =
    FramesDefinition(start = 0, count = 1, frameDuration = 0.1f)

  override val speed: Float = 12f

  override def destroyedOnContact: Boolean = true

  override def copy(params: AbilityParams): Ability = {
    Arrow(params)
  }

}
