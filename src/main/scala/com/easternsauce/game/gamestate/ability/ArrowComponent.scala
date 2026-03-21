package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.FramesDefinition
import com.easternsauce.game.math.Vector2f
import com.softwaremill.quicklens.ModifyPimp

case class ArrowComponent(params: AbilityComponentParams) extends AbilityComponent {

  // --- Constant max range ---
  val maxRange: Float = 30f // tweak this value as needed

  override val textureFileName: String = "arrow"
  override val textureSize: Int = 64
  override val bodyRadius: Float = 0.3f

  override val framesDefinition: FramesDefinition =
    FramesDefinition(start = 0, count = 1, frameDuration = 0.1f)

  override val speed: Float = 12f

  override def init(): ArrowComponent = this

  override def isDestroyedOnCreatureContact: Boolean = true
  override def isDestroyedOnTerrainContact: Boolean = true

  override def update(
      delta: Float,
      newPos: Option[Vector2f]
  )(implicit game: CoreGame): AbilityComponent = {

    val updated = super.update(delta, newPos)

    val distanceFromSpawn = updated.params.pos.distance(updated.params.spawnPos)

    if (distanceFromSpawn > maxRange && !updated.params.isScheduledToBeRemoved) {
      updated
        .modify(_.params.isScheduledToBeRemoved)
        .setTo(true)
        .modify(_.params.isContinueScenario)
        .setTo(false)
    } else updated
  }

  override def copy(params: AbilityComponentParams): AbilityComponent =
    ArrowComponent(params)

}
