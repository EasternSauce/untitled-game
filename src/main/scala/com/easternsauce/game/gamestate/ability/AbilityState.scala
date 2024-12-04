package com.easternsauce.game.gamestate.ability

object AbilityState extends Enumeration {
  type AbilityState = Value
  val Channelling, Active, Cancelled, Finished = Value

}
