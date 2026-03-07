package com.easternsauce.game.gamephysics

import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable

case class CreatureRegistry() {
  private var creatureBodies: mutable.Map[GameEntityId[Creature], CreatureBody] = _

  def init(spawner: CreatureSpawner): Unit = {
    creatureBodies = spawner.allBodies
  }

  def positions(areaId: AreaId): Map[GameEntityId[Creature], Vector2f] =
    creatureBodies.values
      .filter(_.areaId == areaId)
      .map(b => b.creatureId -> b.pos)
      .toMap

  def positionsForAllAreas(): Map[GameEntityId[Creature], Vector2f] =
    creatureBodies.values
      .map(b => b.creatureId -> b.pos)
      .toMap
}
