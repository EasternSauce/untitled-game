package com.easternsauce.game.gamestate.ability

object AbilityType extends Enumeration {
  type AbilityType = Value

  protected case class AbilityTypeDetails(
      i: Int,
      name: String,
      cooldown: Float
  ) extends super.Val(i, name)

  import scala.language.implicitConversions

  implicit def valueToDetails(x: Value): AbilityTypeDetails =
    x.asInstanceOf[AbilityTypeDetails]

  val Arrow: Value = AbilityTypeDetails(0, "Arrow", 1f)
  val MeleeAttack: Value = AbilityTypeDetails(1, "MeleeAttack", 0.5f)

}
