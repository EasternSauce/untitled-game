package com.easternsauce.game.core

import com.easternsauce.game.gamestate.ability.AbilityComponent

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class AbilityComponentsToCreateScheduler() {
  private var _abilityComponentsToCreate: mutable.ListBuffer[AbilityComponent] =
    _

  def init(): Unit = {
    _abilityComponentsToCreate = ListBuffer()
  }

  def playersToCreate: List[AbilityComponent] = {
    _abilityComponentsToCreate.toList
  }

  def clearAbilityComponentsToCreate(): Unit = {
    _abilityComponentsToCreate.clear()
  }

  def scheduleAbilityComponentToCreate(
      abilityComponent: AbilityComponent
  ): Unit = {
    _abilityComponentsToCreate += abilityComponent
  }

}
