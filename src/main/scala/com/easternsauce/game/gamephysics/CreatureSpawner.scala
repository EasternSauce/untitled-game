package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}

import scala.collection.mutable

case class CreatureSpawner() {

  private var creatureBodies: mutable.Map[GameEntityId[Creature], CreatureBody] = _
  private var areaWorlds: mutable.Map[AreaId, AreaWorld] = _

  def init(areaWorlds: mutable.Map[AreaId, AreaWorld]): Unit = {
    creatureBodies = mutable.Map()
    this.areaWorlds = areaWorlds
  }

  def spawn(creature: Creature)(implicit game: CoreGame): Unit = {
    val body = CreatureBody(creature.id)

    body.init(
      areaWorlds(creature.currentAreaId),
      creature.pos
    )

    creatureBodies(creature.id) = body
  }

  def remove(creatureId: GameEntityId[Creature]): Unit = {
    creatureBodies.remove(creatureId)
  }

  def allBodies: mutable.Map[GameEntityId[Creature], CreatureBody] = creatureBodies
}