package com.easternsauce.game.gamestate.ability.scenario

import com.easternsauce.game.gamestate.ability.Ability
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

case class AbilityComponentScenarioStepParams(
    abilityId: GameEntityId[Ability],
    currentAreaId: AreaId,
    creatureId: GameEntityId[Creature],
    pos: Vector2f,
    facingVector: Vector2f,
    damage: Float,
    scenarioStepNo: Int
) {}
