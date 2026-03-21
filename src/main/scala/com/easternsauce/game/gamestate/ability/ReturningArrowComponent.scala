package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.FramesDefinition
import com.easternsauce.game.math.Vector2f
import com.softwaremill.quicklens.ModifyPimp

case class ReturningArrowComponent(params: AbilityComponentParams) extends AbilityComponent {

  override val textureFileName: String = "arrow"
  override val textureSize: Int = 64
  override val bodyRadius: Float = 0.3f

  override val framesDefinition: FramesDefinition =
    FramesDefinition(start = 0, count = 1, frameDuration = 0.1f)

  override val speed: Float = 18f

  override def init(): ReturningArrowComponent = this

  override def isDestroyedOnCreatureContact: Boolean = true

  override def isDestroyedOnTerrainContact: Boolean =
    params.hasLeftInitialSurface

  override def update(
      delta: Float,
      newPos: Option[Vector2f]
  )(implicit game: CoreGame): AbilityComponent = {

    val afterBase = super.update(delta, newPos)

    val owner = game.gameState.creatures(params.creatureId)

    // ✅ correct vector math
    val direction =
      afterBase.params.pos.vectorTowards(owner.pos).normalized

    val withDirection =
      afterBase
        .modify(_.params.facingVector)
        .setTo(direction)

    val withSurfaceExit =
      if (
        !withDirection.params.hasLeftInitialSurface &&
        withDirection.params.pos.distance(withDirection.params.spawnPos) > 0.3f
      ) {
        withDirection.modify(_.params.hasLeftInitialSurface).setTo(true)
      } else withDirection

    val result =
      if (withSurfaceExit.params.pos.distance(owner.pos) < 0.3f) {
        withSurfaceExit.modify(_.params.isScheduledToBeRemoved).setTo(true)
      } else withSurfaceExit

    result
  }

  override def copy(params: AbilityComponentParams): AbilityComponent =
    ReturningArrowComponent(params)
}
