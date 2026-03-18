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

  private var creaturePhysics: CreaturePhysicsController = _
  private var abilityPhysics: AbilityPhysicsController = _

  // -------------------------
  // Init
  // -------------------------

  def init(tiledMaps: mutable.Map[AreaId, GameTiledMap])(implicit
      game: CoreGame
  ): Unit = {

    areaWorlds = mutable.Map() ++ tiledMaps.map { case (areaId, _) =>
      (areaId, AreaWorld(areaId))
    }

    // Physics controllers
    creaturePhysics = CreaturePhysicsController()
    creaturePhysics.init(areaWorlds)

    abilityPhysics = AbilityPhysicsController()
    abilityPhysics.init(
      areaWorlds,
      game.gameState.abilityComponents.values
    )

    // Static bodies
    initStaticBodies(tiledMaps)

    areaWorlds.values.foreach(_.buildStaticChunks())
  }

  /** Creates terrain and static object bodies for all areas. */
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

  // -------------------------
  // Update Loop
  // -------------------------

  def update(areaId: AreaId)(implicit game: CoreGame): Unit = {

    val area = areaWorlds(areaId)

    area.update()

    handleEvents(areaId)

    // Sync game state ↔ physics
    creaturePhysics.synchronize(areaId)
    abilityPhysics.synchronize(areaId)

    // Physics step
    creaturePhysics.update(areaId)
    abilityPhysics.update(areaId)

    // Optional correction pass
    creaturePhysics.correctPositions(areaId)
    abilityPhysics.correctPositions(areaId)
  }

  // -------------------------
  // Event Handling
  // -------------------------

  private def handleEvents(areaId: AreaId)(implicit
      game: CoreGame
  ): Unit = {

    val events =
      game.queues.physicsEventQueue.drain()

    events.foreach {
      case CreatureTeleportEvent(id, pos) =>
        creaturePhysics.teleportIfInArea(id, pos, areaId)

      case AbilityTeleportEvent(id, pos) =>
        abilityPhysics.setBodyPosIfInArea(id, pos, areaId)

      case _ =>
    }
  }

  // -------------------------
  // External Queries
  // -------------------------

  def creatureBodyPositions: Map[GameEntityId[Creature], Vector2f] =
    creaturePhysics.bodyPositionsForAllAreas

  def abilityBodyPositions: Map[GameEntityId[AbilityComponent], Vector2f] =
    abilityPhysics.bodyPositionsForAllAreas

  def creatureBodies: Map[GameEntityId[Creature], CreatureBody] =
    creaturePhysics.bodiesMap

  def correctBodyPositions(areaId: AreaId)(implicit
      game: CoreGame
  ): Unit = {
    creaturePhysics.correctPositions(areaId)
    abilityPhysics.correctPositions(areaId)
  }
}
