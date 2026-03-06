package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable

case class WorldSimulation() {

  var areaWorlds: mutable.Map[AreaId, AreaWorld] = _

  private var creatureSpawner: CreatureSpawner = _
  private var creatureUpdater: CreatureUpdater = _
  private var creatureRegistry: CreatureRegistry = _

  private var abilityBodyPhysics: AbilityBodyPhysics = _

  def init(tiledMaps: mutable.Map[AreaId, GameTiledMap])(implicit game: CoreGame): Unit = {

    areaWorlds = mutable.Map() ++ tiledMaps.map { case (areaId, _) =>
      (areaId, AreaWorld(areaId))
    }

    areaWorlds.values.foreach(_.init(PhysicsContactListener()))

    creatureSpawner = CreatureSpawner()
    creatureSpawner.init(areaWorlds)

    creatureUpdater = CreatureUpdater()
    creatureUpdater.init(creatureSpawner.allBodies, areaWorlds)

    creatureRegistry = CreatureRegistry()
    creatureRegistry.init(creatureSpawner)

    abilityBodyPhysics = AbilityBodyPhysics()
    abilityBodyPhysics.init(areaWorlds)
  }

  def update(areaId: AreaId)(implicit game: CoreGame): Unit = {
    val area = areaWorlds(areaId)

    // Step 1: step physics world
    area.update()

    // Step 2: handle physics events
    handleEvents(areaId)

    // Step 3: correct drift
    correctBodyPositions(areaId)

    // Step 4: synchronize game state <-> physics bodies
    synchronize(areaId)

    // Step 5: update body logic
    updateBodies(areaId)
  }

  private def handleEvents(areaId: AreaId)(implicit game: CoreGame): Unit = {

    val events = game.queues.physicsEvents.toList
    game.queues.physicsEvents.clear()

    events.foreach {
      case TeleportEvent(creatureId, pos) =>
        creatureUpdater.teleportIfInArea(creatureId, pos, areaId, game)

      case MakeBodySensorEvent(creatureId) =>
        creatureUpdater.setSensorIfInArea(creatureId, areaId, game)

      case MakeBodyNonSensorEvent(creatureId) =>
        creatureUpdater.setNonSensorIfInArea(creatureId, areaId, game)

      case _ =>
    }
  }

  def creatureBodyPositions: Map[GameEntityId[Creature], Vector2f] =
    creatureRegistry.positionsForAllAreas()

  def abilityBodyPositions: Map[GameEntityId[AbilityComponent], Vector2f] =
    abilityBodyPhysics.abilityBodyPositions

  def correctBodyPositions(areaId: AreaId)(implicit game: CoreGame): Unit = {

    creatureUpdater.correctPositions(areaId)

    abilityBodyPhysics.correctBodyPositions(areaId)
  }

  private def synchronize(areaId: AreaId)(implicit game: CoreGame): Unit = {
    creatureUpdater.synchronize(areaId)
    abilityBodyPhysics.synchronize(areaId)
  }

  private def updateBodies(areaId: AreaId)(implicit game: CoreGame): Unit = {
    creatureUpdater.update(areaId)
    abilityBodyPhysics.update(areaId)
  }
}
