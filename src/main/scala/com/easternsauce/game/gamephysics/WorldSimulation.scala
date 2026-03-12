package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable

case class WorldSimulation() {

  var areaWorlds: mutable.Map[AreaId, AreaWorld] = _

  private var creatureSpawner: CreatureSpawner = _
  private var creatureUpdater: CreatureUpdater = _
  private var creatureRegistry: CreatureRegistry = _

  private var abilitySpawner: AbilitySpawner = _
  private var abilityUpdater: AbilityUpdater = _
  private var abilityRegistry: AbilityRegistry = _

  // --- static terrain bodies ---
  private var staticEnvironmentBodies: StaticEnvironmentBodies = _

  def init(tiledMaps: mutable.Map[AreaId, GameTiledMap])(implicit game: CoreGame): Unit = {

    areaWorlds = mutable.Map() ++ tiledMaps.map { case (areaId, _) =>
      (areaId, AreaWorld(areaId))
    }

    // --- Creatures ---
    creatureSpawner = CreatureSpawner()
    creatureSpawner.init(areaWorlds)

    creatureUpdater = CreatureUpdater()
    creatureUpdater.init(creatureSpawner.allBodies, areaWorlds)

    creatureRegistry = CreatureRegistry()
    creatureRegistry.init(creatureSpawner)

    // --- Abilities ---
    abilitySpawner = AbilitySpawner()
    abilitySpawner.init(areaWorlds, game.gameState.abilityComponents.values)

    abilityUpdater = AbilityUpdater()
    abilityUpdater.init(abilitySpawner.allBodies, areaWorlds)

    abilityRegistry = AbilityRegistry()
    abilityRegistry.init(abilitySpawner)

    // --- Static terrain ---
    staticEnvironmentBodies = StaticEnvironmentBodies()
    staticEnvironmentBodies.init(tiledMaps, areaWorlds)
  }

  def update(areaId: AreaId)(implicit game: CoreGame): Unit = {
    val area = areaWorlds(areaId)

    // Step 1: step physics world
    area.update()

    // Step 2: handle events
    handleEvents(areaId)

    // Step 3: correct drift
    correctBodyPositions(areaId)

    // Step 4: synchronize game state <-> physics bodies
    synchronize(areaId)

    // Step 5: update body logic
    updateBodies(areaId)
  }

  private def handleEvents(areaId: AreaId)(implicit game: CoreGame): Unit = {
    // drain() returns all events and clears the queue atomically
    val events = game.queues.physicsEventQueue.drain()

    events.foreach {
      case CreatureTeleportEvent(creatureId, pos) =>
        creatureUpdater.teleportIfInArea(creatureId, pos, areaId, game)

      case CreatureMakeSensorEvent(creatureId) =>
        creatureUpdater.setSensorIfInArea(creatureId, areaId, game)

      case CreatureMakeNonSensorEvent(creatureId) =>
        creatureUpdater.setNonSensorIfInArea(creatureId, areaId, game)

      case AbilityTeleportEvent(abilityId, pos) =>
        abilityUpdater.setBodyPosIfInArea(abilityId, pos, areaId, game)

      case AbilityMakeSensorEvent(abilityId) =>
        abilityUpdater.setSensorIfInArea(abilityId, areaId, game)

      case AbilityMakeNonSensorEvent(abilityId) =>
        abilityUpdater.setNonSensorIfInArea(abilityId, areaId, game)

      case _ =>
    }
  }

  private def updateBodies(areaId: AreaId)(implicit game: CoreGame): Unit = {
    creatureUpdater.update(areaId)
    abilityUpdater.update(areaId)
    // static bodies do not need updating
  }

  private def synchronize(areaId: AreaId)(implicit game: CoreGame): Unit = {
    creatureUpdater.synchronize(areaId)
    abilityUpdater.synchronize(areaId)
    // static bodies do not need synchronizing
  }

  def correctBodyPositions(areaId: AreaId)(implicit game: CoreGame): Unit = {
    creatureUpdater.correctPositions(areaId)
    abilityUpdater.correctPositions(areaId)
    // static bodies do not need correction
  }

  def creatureBodyPositions: Map[GameEntityId[Creature], Vector2f] =
    creatureRegistry.positionsForAllAreas()

  def abilityBodyPositions: Map[GameEntityId[AbilityComponent], Vector2f] =
    abilityRegistry.positionsForAllAreas()
}
