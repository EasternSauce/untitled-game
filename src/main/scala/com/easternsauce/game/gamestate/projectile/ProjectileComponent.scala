package com.easternsauce.game.gamestate.projectile

import com.easternsauce.game.gamestate.GameEntity
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId

trait ProjectileComponent extends GameEntity {
  def id: GameEntityId[ProjectileComponent]
  def abilityId: GameEntityId[_]
  def params: ProjectileComponentParams
  def currentAreaId: AreaId

  def update(delta: Float): ProjectileComponent
}

case class BasicProjectileComponent(
    id: GameEntityId[ProjectileComponent],
    abilityId: GameEntityId[_],
    params: ProjectileComponentParams,
    currentAreaId: AreaId
) extends ProjectileComponent {
  override def update(delta: Float): ProjectileComponent = {
    // simple linear motion update
    val (x, y) = params.pos
    val (vx, vy) = params.velocity
    this.copy(params = params.copy(pos = (x + vx * delta, y + vy * delta)))
  }
}
