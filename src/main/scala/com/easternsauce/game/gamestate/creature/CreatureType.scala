package com.easternsauce.game.gamestate.creature

import com.easternsauce.game.Constants
import com.easternsauce.game.gamestate.creature.CreatureAnimationType.CreatureAnimationType
import com.easternsauce.game.gamestate.creature.PrimaryWeaponType.PrimaryWeaponType
import com.easternsauce.game.gamestate.creature.SecondaryWeaponType.SecondaryWeaponType

object CreatureType extends Enumeration {

  protected case class CreatureTypeDetails(
      baseSpeed: Float,
      maxLife: Float,
      damage: Float,
      attackRange: Float,
      texturePaths: Map[CreatureAnimationType, String],
      textureSize: Int,
      spriteVerticalShift: Float,
      bodyRadius: Float,
      animationDefinition: AnimationDefinition,
      primaryWeaponType: PrimaryWeaponType,
      secondaryWeaponType: SecondaryWeaponType,
      isRenderBodyOnly: Boolean
  ) extends super.Val

  import scala.language.implicitConversions

  implicit def valueToDetails(x: Value): CreatureTypeDetails =
    x.asInstanceOf[CreatureTypeDetails]

  type CreatureType = Value

  val Human: Value = CreatureTypeDetails(
    baseSpeed = 8f,
    maxLife = 100f,
    damage = 20f,
    attackRange = 2f,
    texturePaths = Map(
      CreatureAnimationType.Body -> "creature/hero/clothes",
      CreatureAnimationType.Head -> "creature/hero/male_head1",
      CreatureAnimationType.Weapon -> "creature/hero/shortbow",
      CreatureAnimationType.Shield -> "creature/hero/shield"
    ),
    textureSize = 128,
    spriteVerticalShift = 10f,
    bodyRadius = 0.3f,
    animationDefinition = Constants.HumanAnimationDefinition,
    primaryWeaponType = PrimaryWeaponType.Bow,
    secondaryWeaponType = SecondaryWeaponType.None,
    isRenderBodyOnly = false
  )

  val Rat: Value = CreatureTypeDetails(
    baseSpeed = 6f,
    maxLife = 40f,
    damage = 10f,
    attackRange = 1.6f,
    texturePaths = Map(
      CreatureAnimationType.Body -> "creature/rat/rat"
    ),
    textureSize = 192,
    spriteVerticalShift = 35f,
    bodyRadius = 0.6f,
    animationDefinition = Constants.RatAnimationDefinition,
    primaryWeaponType = PrimaryWeaponType.None,
    secondaryWeaponType = SecondaryWeaponType.None,
    isRenderBodyOnly = true
  )

  val Zombie: Value = CreatureTypeDetails(
    baseSpeed = 5f,
    maxLife = 65f,
    damage = 20f,
    attackRange = 1.6f,
    texturePaths = Map(
      CreatureAnimationType.Body -> "creature/zombie/zombie"
    ),
    textureSize = 128,
    spriteVerticalShift = 10f,
    bodyRadius = 0.3f,
    animationDefinition = Constants.ZombieAnimationDefinition,
    primaryWeaponType = PrimaryWeaponType.None,
    secondaryWeaponType = SecondaryWeaponType.None,
    isRenderBodyOnly = true
  )

  val Wyvern: Value = CreatureTypeDetails(
    baseSpeed = 5f,
    maxLife = 200f,
    damage = 40f,
    attackRange = 2.2f,
    texturePaths = Map(
      CreatureAnimationType.Body -> "creature/wyvern/wyvern"
    ),
    textureSize = 256,
    spriteVerticalShift = 10f,
    bodyRadius = 0.8f,
    animationDefinition = Constants.WyvernAnimationDefinition,
    primaryWeaponType = PrimaryWeaponType.None,
    secondaryWeaponType = SecondaryWeaponType.None,
    isRenderBodyOnly = true
  )

}
