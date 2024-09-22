package com.easternsauce.game.gamephysics

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.Ability
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable

case class AbilityBodyPhysics() {
  private var abilityBodies: mutable.Map[GameEntityId[Ability], AbilityBody] = _
  private var areaWorlds: mutable.Map[AreaId, AreaWorld] = _
  private var abilityBodySynchronizer: AbilityBodySynchronizer = _

  def init(areaWorlds: mutable.Map[AreaId, AreaWorld]): Unit = {
    abilityBodies = mutable.Map()
    this.areaWorlds = areaWorlds
    abilityBodySynchronizer = AbilityBodySynchronizer()
    abilityBodySynchronizer.init(abilityBodies, areaWorlds)
  }

  def update(areaId: AreaId)(implicit game: CoreGame): Unit = {
    abilityBodies
      .filter { case (abilityId, _) =>
        game.gameState.abilities(abilityId).params.currentAreaId == areaId
      }
      .values
      .foreach(_.update(game.gameState))
  }

  def synchronize(areaId: AreaId)(implicit game: CoreGame): Unit = {
    abilityBodySynchronizer.synchronizeForArea(areaId)
  }

  def correctBodyPositions(areaId: AreaId)(implicit game: CoreGame): Unit = {
    game.gameState.abilities.values.foreach(creature =>
      if (
        creature.params.currentAreaId == areaId &&
        abilityBodies.contains(creature.id) && abilityBodies(creature.id).pos
          .distance(creature.pos) > Constants.PhysicsBodyCorrectionDistance
      ) {
        abilityBodies(creature.id).setPos(creature.pos)
      }
    )
  }

  def abilityBodyPositions: Map[GameEntityId[Ability], Vector2f] = {
    abilityBodies.values
      .map(abilityBody => {
        val pos = abilityBody.pos
        (abilityBody.abilityId, pos)
      })
      .toMap
  }

  def setBodyPos(abilityId: GameEntityId[Ability], pos: Vector2f): Unit = {
    abilityBodies(abilityId).setPos(pos)
  }

  def setSensor(abilityId: GameEntityId[Ability]): Unit = {
    abilityBodies(abilityId).setSensor()
  }

  def setNonSensor(abilityId: GameEntityId[Ability]): Unit = {
    abilityBodies(abilityId).setNonSensor()
  }
}
