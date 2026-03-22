package com.easternsauce.game.gamephysics

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.gamestate.projectile.ProjectileComponent
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable

case class AbilityPhysicsController() {

  private var bodies: mutable.Map[GameEntityId[ProjectileComponent], AbilityBody] =
    _
  private var areaPhysicsWorlds: mutable.Map[AreaId, AreaPhysicsWorld] = _

  def init(
      areaPhysicsWorlds: mutable.Map[AreaId, AreaPhysicsWorld],
      existingAbilities: Iterable[ProjectileComponent]
  ): Unit = {
    bodies = mutable.Map()
    this.areaPhysicsWorlds = areaPhysicsWorlds

    existingAbilities.foreach(spawn)
  }

  // -------------------------
  // Lifecycle
  // -------------------------

  def spawn(projectile: ProjectileComponent): Unit = {
    val body = AbilityBody(projectile.id)

    body.init(
      areaPhysicsWorlds(projectile.currentAreaId),
      projectile.pos,
      projectile.velocity,
      projectile.bodyRadius
    )

    bodies(projectile.id) = body
  }

  def remove(id: GameEntityId[ProjectileComponent]): Unit = {
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

    val entities = game.gameState.projectileComponents

    val toCreate =
      entities.values.filter(e => e.params.currentAreaId == areaId && !bodies.contains(e.id))

    val toDestroy =
      bodies.values.filter(b => !entities.contains(b.projectileComponentId) || b.areaId != areaId)

    toCreate.foreach(spawn)

    toDestroy.foreach { body =>
      body.onRemove()
      bodies.remove(body.projectileComponentId)
    }

    val toUpdate =
      entities.values.filter(a => bodies.contains(a.id))

    toUpdate.foreach { a =>
      bodies(a.id).setVelocity(a.velocity)
    }
  }

  def correctPositions(areaId: AreaId)(implicit game: CoreGame): Unit = {
    game.gameState.projectileComponents.values.foreach { ability =>
      if (
        ability.params.currentAreaId == areaId &&
        bodies.contains(ability.id)
      ) {
        val body = bodies(ability.id)
        if (
          body.pos.distance(ability.pos) >
            Constants.PhysicsBodyCorrectionDistance
        ) {
          body.setPos(ability.pos)
        }
      }
    }
  }

  def setBodyPosIfInArea(
      id: GameEntityId[ProjectileComponent],
      pos: Vector2f,
      areaId: AreaId
  )(implicit game: CoreGame): Unit = {
    if (
      game.gameState.projectileComponents
        .get(id)
        .exists(_.params.currentAreaId == areaId)
    ) {
      bodies(id).setPos(pos)
    }
  }

  // -------------------------
  // Queries
  // -------------------------

  def bodyPositionsForAllAreas: Map[GameEntityId[ProjectileComponent], Vector2f] =
    bodies.view.mapValues(_.pos).toMap

  def bodiesMap: Map[GameEntityId[ProjectileComponent], AbilityBody] =
    bodies.toMap
}
