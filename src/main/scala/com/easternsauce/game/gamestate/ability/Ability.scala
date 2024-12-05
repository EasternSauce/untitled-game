package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameEntity
import com.easternsauce.game.gamestate.ability.AbilityState.AbilityState
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.softwaremill.quicklens.ModifyPimp

trait Ability extends GameEntity {
  val params: AbilityParams

  def onActiveStart()(implicit game: CoreGame): Ability

  def update(delta: Float)(implicit game: CoreGame): Ability = {
    updateTimers(delta)
      .updateActivation()
  }

  def id: GameEntityId[Ability] = params.id
  def currentAreaId: AreaId = params.currentAreaId

  def currentState: AbilityState = params.state

  def currentStateTime: Float = params.stateTimer.time

  def channelTime: Float

  private def updateTimers(delta: Float)(implicit game: CoreGame): Ability = {
    this
      .modify(_.params.stateTimer)
      .using(_.update(delta))
  }

  private def updateActivation()(implicit game: CoreGame): Ability = {
    this.transformIf(
      currentState == AbilityState.Channelling && currentStateTime > channelTime
    ) {
      this
        .modify(_.params.state)
        .setTo(AbilityState.Active)
        .modify(_.params.stateTimer)
        .using(_.restart())
        .onActiveStart()
    }
  }

  def copy(params: AbilityParams): Ability

}
