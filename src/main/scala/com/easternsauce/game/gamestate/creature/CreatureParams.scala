package com.easternsauce.game.gamestate.creature

import com.easternsauce.game.gamestate.SimpleTimer
import com.easternsauce.game.gamestate.ability.AbilityType
import com.easternsauce.game.gamestate.ability.AbilityType.AbilityType
import com.easternsauce.game.gamestate.creature.CreatureAnimationType.CreatureAnimationType
import com.easternsauce.game.gamestate.creature.PrimaryWeaponType.PrimaryWeaponType
import com.easternsauce.game.gamestate.creature.SecondaryWeaponType.SecondaryWeaponType
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

case class CreatureParams(
    id: GameEntityId[Creature],
    currentAreaId: AreaId,
    pos: Vector2f,
    velocity: Vector2f = Vector2f(0, 0),
    destination: Vector2f,
    lastPos: Vector2f,
    textureSize: Int,
    spriteVerticalShift: Float,
    bodyRadius: Float,
    player: Boolean,
    baseSpeed: Float,
    life: Float,
    maxLife: Float,
    damage: Float,
    facingVector: Vector2f = Vector2f(1, 0),
    texturePaths: Map[CreatureAnimationType, String],
    animationDefinition: AnimationDefinition,
    attackRange: Float,
    primaryWeaponType: PrimaryWeaponType,
    secondaryWeaponType: SecondaryWeaponType,
    renderBodyOnly: Boolean,
    animationTimer: SimpleTimer = SimpleTimer(running = true),
    attackAnimationTimer: SimpleTimer = SimpleTimer(running = false),
    deathAnimationTimer: SimpleTimer = SimpleTimer(running = false),
    respawnDelayInProgress: Boolean = false,
    destinationReached: Boolean = true,
    abilityCooldownTimers: Map[AbilityType, SimpleTimer] =
      AbilityType.values.toList
        .map(abilityType => abilityType -> SimpleTimer(running = false))
        .toMap
) {}
