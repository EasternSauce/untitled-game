package com.easternsauce.game.gamephysics

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f
import scala.collection.mutable

case class AbilityUpdater() {

  private var abilityBodies: mutable.Map[GameEntityId[AbilityComponent], AbilityBody] = _
  private var areaWorlds: mutable.Map[AreaId, AreaWorld] = _
  private var synchronizer: AbilityBodySynchronizer = _

  def init(
      abilityBodies: mutable.Map[GameEntityId[AbilityComponent], AbilityBody],
      areaWorlds: mutable.Map[AreaId, AreaWorld]
  ): Unit = {
    this.abilityBodies = abilityBodies
    this.areaWorlds = areaWorlds
    synchronizer = AbilityBodySynchronizer()
    synchronizer.init(abilityBodies, areaWorlds)
  }

  def update(areaId: AreaId)(implicit game: CoreGame): Unit = {
    abilityBodies
      .filter { case (abilityId, _) =>
        game.gameState.abilityComponents(abilityId).params.currentAreaId == areaId
      }
      .values
      .foreach(_.update(game.gameState))
  }

  def synchronize(areaId: AreaId)(implicit game: CoreGame): Unit =
    synchronizer.synchronize(areaId)

  def correctPositions(areaId: AreaId)(implicit game: CoreGame): Unit = {
    game.gameState.abilityComponents.values.foreach(ability =>
      if (
        ability.params.currentAreaId == areaId &&
        abilityBodies.contains(ability.id) &&
        abilityBodies(ability.id).pos
          .distance(ability.pos) > Constants.PhysicsBodyCorrectionDistance
      ) {
        abilityBodies(ability.id).setPos(ability.pos)
      }
    )
  }

  def setBodyPosIfInArea(
      abilityId: GameEntityId[AbilityComponent],
      pos: Vector2f,
      areaId: AreaId,
      game: CoreGame
  ): Unit = {
    if (
      game.gameState.abilityComponents.contains(abilityId) &&
      game.gameState.abilityComponents(abilityId).params.currentAreaId == areaId
    ) {
      abilityBodies(abilityId).setPos(pos)
    }
  }

  def setSensorIfInArea(
      abilityId: GameEntityId[AbilityComponent],
      areaId: AreaId,
      game: CoreGame
  ): Unit = {
    if (
      game.gameState.abilityComponents.contains(abilityId) &&
      game.gameState.abilityComponents(abilityId).params.currentAreaId == areaId
    ) {
      abilityBodies(abilityId).setSensor()
    }
  }

  def setNonSensorIfInArea(
      abilityId: GameEntityId[AbilityComponent],
      areaId: AreaId,
      game: CoreGame
  ): Unit = {
    if (
      game.gameState.abilityComponents.contains(abilityId) &&
      game.gameState.abilityComponents(abilityId).params.currentAreaId == areaId
    ) {
      abilityBodies(abilityId).setNonSensor()
    }
  }
}
