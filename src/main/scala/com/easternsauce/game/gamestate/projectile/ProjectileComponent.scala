package com.easternsauce.game.gamestate.projectile

import com.easternsauce.game.entitycreator.ProjectileComponentType
import com.easternsauce.game.gamestate.GameEntity
import com.easternsauce.game.gamestate.id.GameEntityId

trait ProjectileComponent extends GameEntity {
  def id: GameEntityId[ProjectileComponent]
  def projectileType: ProjectileComponentType
  def params: ProjectileComponentParams
}
