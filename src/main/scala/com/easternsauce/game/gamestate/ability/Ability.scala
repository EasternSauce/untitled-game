package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameEntity
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}

trait Ability extends GameEntity {
  val params: AbilityParams

  def init()(implicit game: CoreGame): Ability

  def update()(implicit game: CoreGame): Ability

  def id: GameEntityId[Ability] = params.id
  def currentAreaId: AreaId = params.currentAreaId

  def copy(params: AbilityParams): Ability

}
