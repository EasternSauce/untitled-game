package com.easternsauce.game.gamephysics

import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable

case class AbilityRegistry() {

  private var abilityBodies: mutable.Map[GameEntityId[AbilityComponent], AbilityBody] = _

  def init(spawner: AbilitySpawner): Unit =
    abilityBodies = spawner.allBodies

  def positions(areaId: AreaId): Map[GameEntityId[AbilityComponent], Vector2f] =
    abilityBodies.values
      .filter(_.areaId == areaId)
      .map(b => b.abilityComponentId -> b.pos)
      .toMap

  def positionsForAllAreas(): Map[GameEntityId[AbilityComponent], Vector2f] =
    abilityBodies.values
      .map(b => b.abilityComponentId -> b.pos)
      .toMap
}
