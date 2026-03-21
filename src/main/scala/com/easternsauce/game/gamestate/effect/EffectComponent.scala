package com.easternsauce.game.gamestate.effect

import com.easternsauce.game.entitycreator.EffectComponentType
import com.easternsauce.game.gamestate.GameEntity
import com.easternsauce.game.gamestate.id.GameEntityId

trait EffectComponent extends GameEntity {
  def id: GameEntityId[EffectComponent]
  def effectType: EffectComponentType
  def params: EffectComponentParams
}
