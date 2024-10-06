package com.easternsauce.game.gamestate.creature

import com.easternsauce.game.gamestate.creature.CreatureType.CreatureType
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

object CreatureFactory {
  def produce(
      creatureId: GameEntityId[Creature],
      currentAreaId: AreaId,
      pos: Vector2f,
      player: Boolean,
      creatureType: CreatureType
  ): Creature = {
    Creature(
      CreatureParams(
        id = creatureId,
        currentAreaId = currentAreaId,
        pos = pos,
        destination = pos,
        lastPos = pos,
        texturePaths = creatureType.texturePaths,
        textureSize = creatureType.textureSize,
        spriteVerticalShift = creatureType.spriteVerticalShift,
        bodyRadius = creatureType.bodyRadius,
        player = player,
        baseSpeed = creatureType.baseSpeed,
        life = creatureType.maxLife,
        maxLife = creatureType.maxLife,
        damage = creatureType.damage,
        animationDefinition = creatureType.animationDefinition,
        attackRange = creatureType.attackRange,
        primaryWeaponType = creatureType.primaryWeaponType,
        secondaryWeaponType = creatureType.secondaryWeaponType,
        renderBodyOnly = creatureType.renderBodyOnly
      )
    )
  }
}
