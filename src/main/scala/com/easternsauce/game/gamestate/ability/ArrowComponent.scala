package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.FramesDefinition
import com.easternsauce.game.gamestate.projectile.ProjectileComponent
import com.easternsauce.game.gamestate.projectile.ProjectileComponentParams
import com.easternsauce.game.math.Vector2f

case class ArrowComponent(params: ProjectileComponentParams) extends ProjectileComponent {

  override val textureFileName: String = "arrow"
  override val textureSize: Int = 64
  override val bodyRadius: Float = 0.3f
  override val framesDefinition: FramesDefinition = FramesDefinition(0, 1, 0.1f)
  override val speed: Float = 12f
  override def isDestroyedOnCreatureContact: Boolean = true
  override def isDestroyedOnTerrainContact: Boolean = true

  val maxRange: Float = 30f

  override def update(
      delta: Float,
      newPos: Option[Vector2f]
  )(implicit game: CoreGame): ProjectileComponent = {

    val updated = super.update(delta, newPos)
    val distance = updated.params.pos.distance(updated.params.spawnPos)

    if (distance > maxRange && !updated.params.isScheduledToBeRemoved) {
      updated.copy(
        params = updated.params.copy(
          isScheduledToBeRemoved = true
        )
      )
    } else updated
  }

  override def copy(params: ProjectileComponentParams): ProjectileComponent =
    ArrowComponent(params)

  override def init(): ProjectileComponent = this
}
