package com.easternsauce.game.gamephysics

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable

case class CreaturePhysicsController() {

  private var bodies: mutable.Map[GameEntityId[Creature], CreatureBody] = _
  private var areaPhysicsWorlds: mutable.Map[AreaId, AreaPhysicsWorld] = _

  def init(areaPhysicsWorlds: mutable.Map[AreaId, AreaPhysicsWorld]): Unit = {
    bodies = mutable.Map()
    this.areaPhysicsWorlds = areaPhysicsWorlds
  }

  // -------------------------
  // Lifecycle
  // -------------------------

  def spawn(creature: Creature): Unit = {
    val body = CreatureBody(
      creature.id,
      creature.params.isPlayer
    )

    body.init(
      areaPhysicsWorlds(creature.currentAreaId),
      creature.pos,
      creature.params.velocity,
      creature.params.bodyRadius
    )

    bodies(creature.id) = body
  }

  def remove(id: GameEntityId[Creature]): Unit = {
    bodies.remove(id).foreach(_.onRemove())
  }

  // -------------------------
  // Simulation
  // -------------------------

  def update(areaId: AreaId)(implicit game: CoreGame): Unit = {
    bodies.values
      .filter(_.areaId == areaId)
      .foreach(_.update(1f / 60f))
  }

  // -------------------------
  // Synchronization
  // -------------------------

  def synchronize(areaId: AreaId)(implicit game: CoreGame): Unit = {

    val entities = game.gameState.creatures

    val toCreate =
      entities.values.filter(c => c.params.currentAreaId == areaId && !bodies.contains(c.id))

    val toDestroy =
      bodies.values.filter(b => !entities.contains(b.creatureId) || b.areaId != areaId)

    val toUpdate =
      entities.values.filter(c => bodies.contains(c.id))

    toCreate.foreach(spawn)

    toUpdate.foreach { c =>
      bodies(c.id).setVelocity(c.params.velocity)
    }

    toDestroy.foreach { body =>
      body.onRemove()
      bodies.remove(body.creatureId)
    }
  }

  def correctPositions(areaId: AreaId)(implicit game: CoreGame): Unit = {
    game.gameState.creatures.values.foreach { c =>
      if (
        c.params.currentAreaId == areaId &&
        bodies.contains(c.id)
      ) {
        val body = bodies(c.id)
        if (
          body.pos.distance(c.pos) >
            Constants.PhysicsBodyCorrectionDistance
        ) {
          body.setPos(c.pos)
        }
      }
    }
  }

  def teleportIfInArea(
      id: GameEntityId[Creature],
      pos: Vector2f,
      areaId: AreaId
  )(implicit game: CoreGame): Unit = {
    if (
      game.gameState.creatures
        .get(id)
        .exists(_.params.currentAreaId == areaId)
    ) {
      bodies(id).setPos(pos)
    }
  }

  // -------------------------
  // Queries
  // -------------------------

  def bodyPositionsForAllAreas: Map[GameEntityId[Creature], Vector2f] =
    bodies.view.mapValues(_.pos).toMap

  def bodiesMap: Map[GameEntityId[Creature], CreatureBody] =
    bodies.toMap
}
