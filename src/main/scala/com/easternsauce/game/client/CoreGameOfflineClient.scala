package com.easternsauce.game.client
import com.easternsauce.game.gamestate.event.AreaGameStateEvent
import com.easternsauce.game.gamestate.event.OperationalGameStateEvent
import com.easternsauce.game.gamestate.id.AreaId

case class CoreGameOfflineClient() extends CoreGameClientBase {

  override def isOffline: Boolean = true

  override protected def processEvents(areaId: AreaId): Unit = {
    val broadcastEventsForCurrentArea =
      game.queues.broadcastEventQueue.drain().filter {
        case event: AreaGameStateEvent => event.areaId == areaId
        case _                         => false
      }

    val localEventsForCurrentArea =
      game.queues.localEventQueue.drain().filter {
        case event: AreaGameStateEvent    => event.areaId == areaId
        case _: OperationalGameStateEvent => true
        case _                            => false
      }

    gameplay.gameStateHolder.applyGameStateEvents(
      localEventsForCurrentArea ++ broadcastEventsForCurrentArea
    )
  }
}
