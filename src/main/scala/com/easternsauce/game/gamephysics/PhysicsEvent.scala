package com.easternsauce.game.gamephysics

import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

sealed trait PhysicsEvent

// --- Creature events ---
case class TeleportEvent(creatureId: GameEntityId[Creature], pos: Vector2f) extends PhysicsEvent
case class MakeBodySensorEvent(creatureId: GameEntityId[Creature]) extends PhysicsEvent
case class MakeBodyNonSensorEvent(creatureId: GameEntityId[Creature]) extends PhysicsEvent

// --- Ability events ---
case class AbilityTeleportEvent(abilityId: GameEntityId[AbilityComponent], pos: Vector2f)
    extends PhysicsEvent
case class AbilityMakeSensorEvent(abilityId: GameEntityId[AbilityComponent]) extends PhysicsEvent
case class AbilityMakeNonSensorEvent(abilityId: GameEntityId[AbilityComponent]) extends PhysicsEvent
