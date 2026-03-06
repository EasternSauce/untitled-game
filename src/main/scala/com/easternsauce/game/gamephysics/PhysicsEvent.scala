package com.easternsauce.game.gamephysics

import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

sealed trait PhysicsEvent

// --- Creature-specific events ---
case class CreatureTeleportEvent(creatureId: GameEntityId[Creature], pos: Vector2f)
    extends PhysicsEvent
case class CreatureMakeSensorEvent(creatureId: GameEntityId[Creature]) extends PhysicsEvent
case class CreatureMakeNonSensorEvent(creatureId: GameEntityId[Creature]) extends PhysicsEvent

// --- Ability events (keep as-is for now) ---
case class AbilityTeleportEvent(abilityId: GameEntityId[AbilityComponent], pos: Vector2f)
    extends PhysicsEvent
case class AbilityMakeSensorEvent(abilityId: GameEntityId[AbilityComponent]) extends PhysicsEvent
case class AbilityMakeNonSensorEvent(abilityId: GameEntityId[AbilityComponent]) extends PhysicsEvent
