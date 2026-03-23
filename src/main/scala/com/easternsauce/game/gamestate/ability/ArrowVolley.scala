package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.projectile.ProjectileComponentType

case class ArrowVolley(params: AbilityParams) extends Ability {

  override def onActiveStart()(implicit game: CoreGame): Ability = {

    val baseAngle = params.facingVector.angleDeg

    for (i <- 0 until 7) {
      val angle = baseAngle - 30f + i * 10f

      spawnProjectile(
        ProjectileComponentType.ArrowComponent,
        pos = params.pos,
        facing = params.facingVector.setAngleDeg(angle),
        damage = params.damage,
        expirationTime = None
      )
    }

    this
  }

  override def channelTime: Float = 0.4f
  override def finishWhenComponentsDestroyed: Boolean = true

  override def copy(params: AbilityParams): Ability =
    ArrowVolley(params)
}
