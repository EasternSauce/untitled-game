package com.easternsauce.game.gamestate.effect

import com.easternsauce.game.entitycreator.EffectComponentType
import com.easternsauce.game.gamestate.GameEntity
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId

trait EffectComponent extends GameEntity {
  def id: GameEntityId[EffectComponent]
  def effectType: EffectComponentType
  def params: EffectComponentParams
  def currentAreaId: AreaId

  def update(delta: Float): EffectComponent
}

case class BuffEffectComponent(
    id: GameEntityId[EffectComponent],
    effectType: EffectComponentType,
    params: EffectComponentParams,
    currentAreaId: AreaId
) extends EffectComponent {
  override def update(delta: Float): EffectComponent = {
    val newTime = params.timeElapsed + delta
    val expired = newTime >= params.duration
    this.copy(params = params.copy(timeElapsed = newTime, isScheduledToBeRemoved = expired))
  }
}
