package com.easternsauce.game.gamephysics

import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.id.GameEntityId

case class AbilityBody(
    abilityComponentId: GameEntityId[AbilityComponent]
) extends PhysicsBody
