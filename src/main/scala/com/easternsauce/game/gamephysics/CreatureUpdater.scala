package com.easternsauce.game.gamephysics

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable

case class CreatureUpdater() {
  private var creatureBodies: mutable.Map[GameEntityId[Creature], CreatureBody] = _
  private var areaWorlds: mutable.Map[AreaId, AreaWorld] = _
  private var synchronizer: CreatureBodySynchronizer = _

  def init(
      creatureBodies: mutable.Map[GameEntityId[Creature], CreatureBody],
      areaWorlds: mutable.Map[AreaId, AreaWorld]
  ): Unit = {
    this.creatureBodies = creatureBodies
    this.areaWorlds = areaWorlds
    synchronizer = CreatureBodySynchronizer()
    synchronizer.init(creatureBodies, areaWorlds)
  }

  def update(areaId: AreaId)(implicit game: CoreGame): Unit = {
    creatureBodies
      .filter { case (creatureId, _) =>
        game.gameState.creatures(creatureId).params.currentAreaId == areaId
      }
      .values
      .foreach(_.update(game.gameState))
  }

  def synchronize(areaId: AreaId)(implicit game: CoreGame): Unit =
    synchronizer.synchronizeForArea(areaId)

  def correctPositions(areaId: AreaId)(implicit game: CoreGame): Unit = {
    game.gameState.creatures.values.foreach(creature =>
      if (
        creature.params.currentAreaId == areaId &&
        creatureBodies.contains(creature.id) &&
        creatureBodies(creature.id).pos
          .distance(creature.pos) > Constants.PhysicsBodyCorrectionDistance
      ) {
        creatureBodies(creature.id).setPos(creature.pos)
      }
    )
  }

  def teleportIfInArea(
      creatureId: GameEntityId[Creature],
      pos: Vector2f,
      areaId: AreaId,
      game: CoreGame
  ): Unit = {
    if (
      game.gameState.creatures.contains(creatureId) &&
      game.gameState.creatures(creatureId).params.currentAreaId == areaId
    ) {
      creatureBodies(creatureId).setPos(pos)
    }
  }

  def setSensorIfInArea(
      creatureId: GameEntityId[Creature],
      areaId: AreaId,
      game: CoreGame
  ): Unit = {
    if (
      game.gameState.creatures.contains(creatureId) &&
      game.gameState.creatures(creatureId).params.currentAreaId == areaId
    ) {
      creatureBodies(creatureId).setSensor()
    }
  }

  def setNonSensorIfInArea(
      creatureId: GameEntityId[Creature],
      areaId: AreaId,
      game: CoreGame
  ): Unit = {
    if (
      game.gameState.creatures.contains(creatureId) &&
      game.gameState.creatures(creatureId).params.currentAreaId == areaId
    ) {
      creatureBodies(creatureId).setNonSensor()
    }
  }
}
