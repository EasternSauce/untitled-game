package com.easternsauce.game.core

import com.easternsauce.game.gamestate.event.{AreaGameStateEvent, GameStateEvent, OperationalGameStateEvent}
import com.easternsauce.game.gamestate.id.AreaId

import scala.collection.mutable.ListBuffer

case class GameEventProcessor() {
  private var broadcastEventsQueue: ListBuffer[GameStateEvent] = _
  private var localEventsQueue: ListBuffer[GameStateEvent] = _

  def init(): Unit = {
    broadcastEventsQueue = ListBuffer()
    localEventsQueue = ListBuffer()
  }

  def queuedOperationalEvents: List[GameStateEvent] =
    (broadcastEventsQueue.toList ++ localEventsQueue.toList).filter {
      case _: OperationalGameStateEvent => true
      case _                            => false
    }

  def queuedAreaBroadcastEventsForArea(areaId: AreaId): List[GameStateEvent] = {
    broadcastEventsQueue.toList.filter {
      case event: AreaGameStateEvent => event.areaId == areaId
      case _                         => false
    }
  }

  def queuedAreaLocalEventsForArea(areaId: AreaId): List[GameStateEvent] = {
    localEventsQueue.toList.filter {
      case event: AreaGameStateEvent => event.areaId == areaId
      case _                         => false
    }
  }

  def queuedAreaEvents: List[GameStateEvent] = {
    (broadcastEventsQueue.toList ++ localEventsQueue.toList).filter {
      case _: AreaGameStateEvent => true
      case _                     => false
    }
  }

  def sendBroadcastEvents(events: List[GameStateEvent]): Unit = {
    broadcastEventsQueue ++= events
  }

  def sendLocalEvents(events: List[GameStateEvent]): Unit = {
    localEventsQueue ++= events
  }

  def clearEventQueues(): Unit = {
    broadcastEventsQueue.clear()
    localEventsQueue.clear()
  }

}
