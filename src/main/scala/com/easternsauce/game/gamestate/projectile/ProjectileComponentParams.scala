package com.easternsauce.game.gamestate.projectile

import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId

case class ProjectileComponentParams(
    creatureId: GameEntityId[Creature],
    pos: (Float, Float),
    velocity: (Float, Float),
    damage: Float,
    isScheduledToBeRemoved: Boolean = false
)
