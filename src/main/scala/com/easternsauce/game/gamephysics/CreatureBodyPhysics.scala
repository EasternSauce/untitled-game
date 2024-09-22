package com.easternsauce.game.gamephysics

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable

case class CreatureBodyPhysics() {
  private var creatureBodies
      : mutable.Map[GameEntityId[Creature], CreatureBody] = _
  private var areaWorlds: mutable.Map[AreaId, AreaWorld] = _
  private var creatureBodySynchronizer: CreatureBodySynchronizer = _

  def init(areaWorlds: mutable.Map[AreaId, AreaWorld]): Unit = {
    creatureBodies = mutable.Map()
    this.areaWorlds = areaWorlds
    creatureBodySynchronizer = CreatureBodySynchronizer()
    creatureBodySynchronizer.init(creatureBodies, areaWorlds)
  }

  def update(areaId: AreaId)(implicit game: CoreGame): Unit = {
    creatureBodies
      .filter { case (creatureId, _) =>
        game.gameState.creatures(creatureId).params.currentAreaId == areaId
      }
      .values
      .foreach(_.update(game.gameState))
  }

  def synchronize(areaId: AreaId)(implicit game: CoreGame): Unit = {
    creatureBodySynchronizer.synchronizeForArea(areaId)
  }

  def correctBodyPositions(areaId: AreaId)(implicit game: CoreGame): Unit = {
    game.gameState.creatures.values.foreach(creature =>
      if (
        creature.params.currentAreaId == areaId &&
        creatureBodies.contains(creature.id) && creatureBodies(creature.id).pos
          .distance(creature.pos) > Constants.PhysicsBodyCorrectionDistance
      ) {
        creatureBodies(creature.id).setPos(creature.pos)
      }
    )
  }

  def creatureBodyPositions: Map[GameEntityId[Creature], Vector2f] = {
    creatureBodies.values
      .map(creatureBody => {
        val pos = creatureBody.pos
        (creatureBody.creatureId, pos)
      })
      .toMap
  }

  def setBodyPos(creatureId: GameEntityId[Creature], pos: Vector2f): Unit = {
    creatureBodies(creatureId).setPos(pos)
  }

  def setSensor(creatureId: GameEntityId[Creature]): Unit = {
    creatureBodies(creatureId).setSensor()
  }

  def setNonSensor(creatureId: GameEntityId[Creature]): Unit = {
    creatureBodies(creatureId).setNonSensor()
  }
}
