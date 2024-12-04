package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class GamePhysics() {
  var areaWorlds: mutable.Map[AreaId, AreaWorld] = _

  private var abilityBodyPhysics: AbilityBodyPhysics = _
  private var creatureBodyPhysics: CreatureBodyPhysics = _
  private var staticBodyPhysics: StaticBodyPhysics = _
  private var eventQueue: ListBuffer[PhysicsEvent] = _
  private var collisionQueue: ListBuffer[GameStateEvent] = _

  def init(
      tiledMaps: mutable.Map[AreaId, GameTiledMap]
  )(implicit game: CoreGame): Unit = {
    areaWorlds = mutable.Map() ++ tiledMaps.map { case (areaId: AreaId, _) =>
      (areaId, AreaWorld(areaId))
    }
    areaWorlds.values.foreach(_.init(PhysicsContactListener()))

    abilityBodyPhysics = AbilityBodyPhysics()
    abilityBodyPhysics.init(areaWorlds)

    creatureBodyPhysics = CreatureBodyPhysics()
    creatureBodyPhysics.init(areaWorlds)

    staticBodyPhysics = StaticBodyPhysics()
    staticBodyPhysics.init(tiledMaps, areaWorlds)

    eventQueue = ListBuffer()
    collisionQueue = ListBuffer()
  }

  def update(areaId: AreaId)(implicit game: CoreGame): Unit = {
    areaWorlds(areaId).update()

    handleEvents(eventQueue.toList, areaId)

    correctBodyPositions(areaId)

    synchronize(areaId)

    updateBodies(areaId)
  }

  private def updateBodies(areaId: AreaId)(implicit game: CoreGame): Unit = {
    abilityBodyPhysics.update(areaId)
    creatureBodyPhysics.update(areaId)
  }

  private def synchronize(
      areaId: AreaId
  )(implicit game: CoreGame): Unit = {
    abilityBodyPhysics.synchronize(areaId)
    creatureBodyPhysics.synchronize(areaId)
  }

  def correctBodyPositions(
      areaId: AreaId
  )(implicit game: CoreGame): Unit = {
    abilityBodyPhysics.correctBodyPositions(areaId)
    creatureBodyPhysics.correctBodyPositions(areaId)
  }

  private def handleEvents(
      eventsToBeProcessed: List[PhysicsEvent],
      areaId: AreaId
  )(implicit game: CoreGame): Unit = {
    eventsToBeProcessed.foreach {
      case TeleportEvent(creatureId, pos) =>
        if (
          game.gameState.creatures.contains(creatureId) && game.gameState
            .creatures(creatureId)
            .params
            .currentAreaId == areaId
        ) {
          creatureBodyPhysics.setBodyPos(creatureId, pos)
        }
      case MakeBodySensorEvent(creatureId) =>
        if (
          game.gameState.creatures.contains(creatureId) && game.gameState
            .creatures(creatureId)
            .params
            .currentAreaId == areaId
        ) {
          creatureBodyPhysics.setSensor(creatureId)
        }
      case MakeBodyNonSensorEvent(creatureId) =>
        if (
          game.gameState.creatures.contains(creatureId) && game.gameState
            .creatures(creatureId)
            .params
            .currentAreaId == areaId
        ) {
          creatureBodyPhysics.setNonSensor(creatureId)
        }
      case _ =>
    }

    eventQueue = eventQueue.filter(!eventsToBeProcessed.contains(_))
  }

  def pollCollisionEvents(): List[GameStateEvent] = {
    val collisionEvents = List().appendedAll(collisionQueue)

    collisionQueue.clear()

    collisionEvents
  }

  def scheduleEvents(events: List[PhysicsEvent]): Unit = {
    eventQueue.addAll(events)
  }

  def scheduleCollisions(collisions: List[GameStateEvent]): Unit = {
    collisionQueue.addAll(collisions)
  }

  def creatureBodyPositions: Map[GameEntityId[Creature], Vector2f] =
    creatureBodyPhysics.creatureBodyPositions

  def abilityBodyPositions: Map[GameEntityId[AbilityComponent], Vector2f] =
    abilityBodyPhysics.abilityBodyPositions

}
