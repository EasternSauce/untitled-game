package com.easternsauce.game.core

import com.easternsauce.game.gamestate.event.{AreaGameStateEvent, GameStateEvent, OperationalGameStateEvent}
import com.easternsauce.game.gamestate.id.AreaId

import scala.collection.mutable.ListBuffer

case class EventQueueContainer() {
  private var broadcastEventsQueue: ListBuffer[GameStateEvent] = _
  private var localEventsQueue: ListBuffer[GameStateEvent] = _

  def init(): Unit = {
    this.synchronized {
      broadcastEventsQueue = ListBuffer()
      localEventsQueue = ListBuffer()
    }
  }

  def broadcastEventsForArea(areaId: AreaId): List[GameStateEvent] = {
    this.synchronized {
      broadcastEventsQueue.toList.filter {
        case event: AreaGameStateEvent => event.areaId == areaId
        case _                         => false
      }
    }
  }

  def areaEvents(areaId: AreaId): List[GameStateEvent] = {
    this.synchronized {
      localEventsQueue.toList.filter {
        case event: AreaGameStateEvent    => event.areaId == areaId
        case _: OperationalGameStateEvent => true
        case _                            => false
      }
    }
  }

  def allEvents: List[GameStateEvent] = {
    this.synchronized {
      broadcastEventsQueue.toList ++ localEventsQueue.toList
    }
  }

  def sendBroadcastEvents(events: List[GameStateEvent]): Unit = {
    this.synchronized {
      broadcastEventsQueue ++= events
    }
  }

  def sendLocalEvents(events: List[GameStateEvent]): Unit = {
    this.synchronized {
      localEventsQueue ++= events
    }
  }

  def clearEventQueues(): Unit = {
    this.synchronized {
      broadcastEventsQueue.clear()
      localEventsQueue.clear()
    }
  }

}
