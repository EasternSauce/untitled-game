package com.easternsauce.game.core

import com.easternsauce.game.gamestate.event.{AreaGameStateEvent, GameStateEvent, OperationalGameStateEvent}
import com.easternsauce.game.gamestate.id.AreaId

import scala.collection.mutable.ListBuffer

case class GameEventProcessor() {
  protected var broadcastEventsQueue: ListBuffer[GameStateEvent] = _

  def init(): Unit = {
    broadcastEventsQueue = ListBuffer()
  }

  def queuedOperationalEvents: List[GameStateEvent] =
    broadcastEventsQueue.toList.filter {
      case _: OperationalGameStateEvent => true
      case _                            => false
    }

  def queuedAreaEvents(areaId: AreaId): List[GameStateEvent] = {
    broadcastEventsQueue.toList.filter {
      case event: AreaGameStateEvent => event.areaId == areaId
      case _                         => false
    }
  }

  def sendBroadcastEvent(event: GameStateEvent): Unit = {
    broadcastEventsQueue.addOne(event)
  }

  def clearEventQueues(): Unit = {
    broadcastEventsQueue.clear()
  }

}
