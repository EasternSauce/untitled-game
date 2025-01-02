package com.easternsauce.game.gamestate.ability.scenario

object NextStepCondition extends Enumeration {
  type NextStepCondition = Value
  val NullCondition, ChainCondition, DelayedChainCondition, TimedCondition =
    Value
}
