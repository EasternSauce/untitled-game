package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class GamePhysics() {
  private var _areaWorlds: mutable.Map[AreaId, AreaWorld] = _
  private var creatureBodyPhysics: CreatureBodyPhysics = _
  private var staticBodyPhysics: StaticBodyPhysics = _
  private var eventQueue: ListBuffer[PhysicsEvent] = _
  private var collisionQueue: ListBuffer[GameStateEvent] = _

  def init(
      tiledMaps: mutable.Map[AreaId, GameTiledMap]
  )(implicit game: CoreGame): Unit = {
    _areaWorlds = mutable.Map() ++ tiledMaps.map { case (areaId: AreaId, _) =>
      (areaId, AreaWorld(areaId))
    }
    _areaWorlds.values.foreach(_.init(PhysicsContactListener(this)))

    creatureBodyPhysics = CreatureBodyPhysics()
    creatureBodyPhysics.init(_areaWorlds)

    staticBodyPhysics = StaticBodyPhysics()
    staticBodyPhysics.init(tiledMaps, _areaWorlds)

    eventQueue = ListBuffer()
    collisionQueue = ListBuffer()
  }

  def updateForArea(areaId: AreaId)(implicit game: CoreGame): Unit = {
    areaWorlds(areaId).update()

    handleEvents(eventQueue.toList, areaId)

    correctBodyPositions(areaId)

    synchronize(areaId)

    updateBodies(areaId)
  }

  private def updateBodies(areaId: AreaId)(implicit game: CoreGame): Unit = {
    creatureBodyPhysics.update(areaId)
  }

  private def synchronize(
      areaId: AreaId
  )(implicit game: CoreGame): Unit = {
    creatureBodyPhysics.synchronize(areaId)
  }

  def correctBodyPositions(
      areaId: AreaId
  )(implicit game: CoreGame): Unit = {
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

  def areaWorlds: mutable.Map[AreaId, AreaWorld] = _areaWorlds

  def creatureBodyPositions: Map[GameEntityId[Creature], Vector2f] =
    creatureBodyPhysics.creatureBodyPositions

}
