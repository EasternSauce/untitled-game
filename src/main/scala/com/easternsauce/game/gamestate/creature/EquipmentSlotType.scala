package com.easternsauce.game.gamestate.creature

object EquipmentSlotType extends Enumeration {
  type EquipmentSlotType = Value

  protected case class EquipmentSlotTypeDetails(
      i: Int,
      name: String,
      displayName: String
  ) extends super.Val(i, name)

  import scala.language.implicitConversions

  implicit def valueToDetails(x: Value): EquipmentSlotTypeDetails =
    x.asInstanceOf[EquipmentSlotTypeDetails]

  val Weapon: Value =
    EquipmentSlotTypeDetails(0, name = "Weapon", displayName = "weapon")
  val Shield: Value =
    EquipmentSlotTypeDetails(1, name = "Shield", displayName = "shield")
  val Ring: Value = EquipmentSlotTypeDetails(2, "Ring", displayName = "ring")
  val BodyArmor: Value =
    EquipmentSlotTypeDetails(3, "BodyArmor", displayName = "body armor")
  val Gloves: Value =
    EquipmentSlotTypeDetails(4, "Gloves", displayName = "gloves")
  val Boots: Value = EquipmentSlotTypeDetails(5, "Boots", displayName = "boots")
}
