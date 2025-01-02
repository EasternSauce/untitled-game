package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.gamestate.creature.FramesDefinition

case class ExplosionComponent(params: AbilityComponentParams)
    extends AbilityComponent {
  override val textureFileName: String = "explosion"

  override val textureSize: Int = 64
  override val bodyRadius: Float = 0.6f

  override val framesDefinition: FramesDefinition =
    FramesDefinition(start = 0, count = 12, frameDuration = 0.05f)

  override val speed: Float = 0f

  override def init(): ExplosionComponent = {
    this
  }

  override def isDestroyedOnCreatureContact: Boolean = false

  override def isDestroyedOnTerrainContact: Boolean = false

  override def copy(params: AbilityComponentParams): AbilityComponent = {
    ExplosionComponent(params)
  }

}
