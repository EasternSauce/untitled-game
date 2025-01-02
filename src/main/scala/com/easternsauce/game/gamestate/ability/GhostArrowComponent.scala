package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.gamestate.creature.FramesDefinition

case class GhostArrowComponent(params: AbilityComponentParams)
    extends AbilityComponent {
  override val textureFileName: String = "arrow"

  override val textureSize: Int = 64
  override val bodyRadius: Float = 0.3f

  override val framesDefinition: FramesDefinition =
    FramesDefinition(start = 0, count = 1, frameDuration = 0.1f)

  override val speed: Float = 12f

  override def init(): GhostArrowComponent = {
    this
  }

  override def isDestroyedOnCreatureContact: Boolean = false

  override def isDestroyedOnTerrainContact: Boolean = true

  override def copy(params: AbilityComponentParams): AbilityComponent = {
    GhostArrowComponent(params)
  }

}
