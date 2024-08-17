package com.easternsauce.game.gamestate.creature

import com.easternsauce.game.Constants
import com.easternsauce.game.gamestate.creature.CreatureAnimationType.CreatureAnimationType
import com.easternsauce.game.gamestate.creature.PrimaryWeaponType.PrimaryWeaponType
import com.easternsauce.game.gamestate.creature.SecondaryWeaponType.SecondaryWeaponType
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

object CreatureFactory {
  def male1(
      creatureId: GameEntityId[Creature],
      currentAreaId: AreaId,
      pos: Vector2f,
      player: Boolean,
      baseSpeed: Float
  ): Creature = {
    CreatureFactory.produce(
      creatureId = creatureId,
      currentAreaId = currentAreaId,
      pos = pos,
      player = player,
      baseSpeed = baseSpeed,
      maxLife = 100f,
      damage = 20f,
      attackRange = 2f,
      texturePaths = Map(
        CreatureAnimationType.Body -> "creature/hero/clothes",
        CreatureAnimationType.Head -> "creature/hero/male_head1",
        CreatureAnimationType.Weapon -> "creature/hero/shortbow",
        CreatureAnimationType.Shield -> "creature/hero/shield"
      ),
      size = 128,
      spriteVerticalShift = 10f,
      bodyRadius = 0.3f,
      animationDefinition = Constants.HumanAnimationDefinition,
      primaryWeaponType = PrimaryWeaponType.Bow,
      secondaryWeaponType = SecondaryWeaponType.None,
      renderBodyOnly = false
    )
  }

  def rat(
      creatureId: GameEntityId[Creature],
      currentAreaId: AreaId,
      pos: Vector2f,
      player: Boolean,
      baseSpeed: Float
  ): Creature = {
    CreatureFactory.produce(
      creatureId = creatureId,
      currentAreaId = currentAreaId,
      pos = pos,
      player = player,
      baseSpeed = baseSpeed,
      maxLife = 40f,
      damage = 10f,
      attackRange = 1.6f,
      texturePaths = Map(
        CreatureAnimationType.Body -> "creature/rat/rat"
      ),
      size = 192,
      spriteVerticalShift = 35f,
      bodyRadius = 0.6f,
      animationDefinition = Constants.RatAnimationDefinition,
      primaryWeaponType = PrimaryWeaponType.None,
      secondaryWeaponType = SecondaryWeaponType.None,
      renderBodyOnly = true
    )
  }

  def zombie( // TODO: add random chances for alternate attack/death animation
      creatureId: GameEntityId[Creature],
      currentAreaId: AreaId,
      pos: Vector2f,
      player: Boolean,
      baseSpeed: Float
  ): Creature = {
    CreatureFactory.produce(
      creatureId = creatureId,
      currentAreaId = currentAreaId,
      pos = pos,
      player = player,
      baseSpeed = baseSpeed,
      maxLife = 65f,
      damage = 20f,
      attackRange = 1.6f,
      texturePaths = Map(
        CreatureAnimationType.Body -> "creature/zombie/zombie"
      ),
      size = 128,
      spriteVerticalShift = 10f,
      bodyRadius = 0.3f,
      animationDefinition = Constants.ZombieAnimationDefinition,
      primaryWeaponType = PrimaryWeaponType.None,
      secondaryWeaponType = SecondaryWeaponType.None,
      renderBodyOnly = true
    )
  }

  def wyvern( // TODO: add random chances for alternate attack/death animation
      creatureId: GameEntityId[Creature],
      currentAreaId: AreaId,
      pos: Vector2f,
      player: Boolean,
      baseSpeed: Float
  ): Creature = {
    CreatureFactory.produce(
      creatureId = creatureId,
      currentAreaId = currentAreaId,
      pos = pos,
      player = player,
      baseSpeed = baseSpeed,
      maxLife = 200f,
      damage = 40f,
      attackRange = 2.2f,
      texturePaths = Map(
        CreatureAnimationType.Body -> "creature/wyvern/wyvern"
      ),
      size = 256,
      spriteVerticalShift = 10f,
      bodyRadius = 0.8f,
      animationDefinition = Constants.WyvernAnimationDefinition,
      primaryWeaponType = PrimaryWeaponType.None,
      secondaryWeaponType = SecondaryWeaponType.None,
      renderBodyOnly = true
    )
  }

  private def produce(
      creatureId: GameEntityId[Creature],
      currentAreaId: AreaId,
      pos: Vector2f,
      player: Boolean,
      baseSpeed: Float,
      maxLife: Float,
      damage: Float,
      attackRange: Float,
      texturePaths: Map[CreatureAnimationType, String],
      size: Int,
      spriteVerticalShift: Float,
      bodyRadius: Float,
      animationDefinition: AnimationDefinition,
      primaryWeaponType: PrimaryWeaponType,
      secondaryWeaponType: SecondaryWeaponType,
      renderBodyOnly: Boolean
  ): Creature = {
    Creature(
      CreatureParams(
        id = creatureId,
        currentAreaId = currentAreaId,
        pos = pos,
        destination = pos,
        lastPos = pos,
        texturePaths = texturePaths,
        size = size,
        spriteVerticalShift = spriteVerticalShift,
        bodyRadius = bodyRadius,
        player = player,
        baseSpeed = baseSpeed,
        life = maxLife,
        maxLife = maxLife,
        damage = damage,
        animationDefinition = animationDefinition,
        attackRange = attackRange,
        primaryWeaponType = primaryWeaponType,
        secondaryWeaponType = secondaryWeaponType,
        renderBodyOnly = renderBodyOnly
      )
    )
  }
}
