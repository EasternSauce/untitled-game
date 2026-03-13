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

  def init(tiledMaps: mutable.Map[AreaId, GameTiledMap])(implicit game: CoreGame): Unit = {

    areaWorlds = mutable.Map() ++ tiledMaps.map { case (areaId, _) =>
      (areaId, AreaWorld(areaId))
    }

    // Creatures
    creatureSpawner = CreatureSpawner()
    creatureSpawner.init(areaWorlds)

    creatureUpdater = CreatureUpdater()
    creatureUpdater.init(creatureSpawner.allBodies, areaWorlds)

    creatureRegistry = CreatureRegistry()
    creatureRegistry.init(creatureSpawner)

    // Abilities
    abilitySpawner = AbilitySpawner()
    abilitySpawner.init(areaWorlds, game.gameState.abilityComponents.values)

    abilityUpdater = AbilityUpdater()
    abilityUpdater.init(abilitySpawner.allBodies, areaWorlds)

    abilityRegistry = AbilityRegistry()
    abilityRegistry.init(abilitySpawner)

    // Static bodies
    initStaticBodies(tiledMaps)
  }

  /** Creates terrain and static object bodies for all areas. Kept separate from init() for clarity.
    */
  private def initStaticBodies(
      tiledMaps: mutable.Map[AreaId, GameTiledMap]
  )(implicit game: CoreGame): Unit = {

    tiledMaps.foreach { case (areaId, map) =>
      val world = areaWorlds(areaId)

      val terrainCells =
        MapCellExtractor.terrainTiles(map)

      val objectCells =
        MapCellExtractor.staticObjects(map)

      val positions =
        (terrainCells ++ objectCells)
          .map(_.pos())
          .distinct

      positions.foreach { pos =>
        val body =
          StaticBody(s"static_${areaId}_${pos.x}_${pos.y}")

        body.init(world, pos)
      }
    }
  }

  def update(areaId: AreaId)(implicit game: CoreGame): Unit = {
    val area = areaWorlds(areaId)

    area.update()

    handleEvents(areaId)
    correctBodyPositions(areaId)
    synchronize(areaId)
    updateBodies(areaId)
  }

  private def handleEvents(areaId: AreaId)(implicit game: CoreGame): Unit = {

    val events =
      game.queues.physicsEventQueue.drain()

    events.foreach {
      case CreatureTeleportEvent(id, pos) =>
        creatureUpdater.teleportIfInArea(id, pos, areaId, game)

      case AbilityTeleportEvent(id, pos) =>
        abilityUpdater.setBodyPosIfInArea(id, pos, areaId, game)

      case _ =>
    }
  }

  private def updateBodies(areaId: AreaId)(implicit game: CoreGame): Unit = {
    creatureUpdater.update(areaId)
    abilityUpdater.update(areaId)
  }

  private def synchronize(areaId: AreaId)(implicit game: CoreGame): Unit = {
    creatureUpdater.synchronize(areaId)
    abilityUpdater.synchronize(areaId)
  }

  def correctBodyPositions(areaId: AreaId)(implicit game: CoreGame): Unit = {
    creatureUpdater.correctPositions(areaId)
    abilityUpdater.correctPositions(areaId)
  }

  def creatureBodyPositions: Map[GameEntityId[Creature], Vector2f] =
    creatureRegistry.positionsForAllAreas()

  def abilityBodyPositions: Map[GameEntityId[AbilityComponent], Vector2f] =
    abilityRegistry.positionsForAllAreas()
}
