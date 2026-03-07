package com.easternsauce.game.client
import com.easternsauce.game.command.ActionsPerformRequestCommand
import com.easternsauce.game.gamestate.event.{AreaGameStateEvent, OperationalGameStateEvent}
import com.easternsauce.game.gamestate.id.AreaId

case class CoreGameClient() extends CoreGameClientBase {

  override def isOffline: Boolean = false

  override protected def processEvents(areaId: AreaId): Unit = {
    val broadcastEventsForCurrentArea =
      game.queues.broadcastEvents.toList.filter {
        case event: AreaGameStateEvent => event.areaId == areaId
        case _                         => false
      }

    if (broadcastEventsForCurrentArea.nonEmpty) {
      client.sendTCP(
        ActionsPerformRequestCommand(broadcastEventsForCurrentArea)
      )
    }

    gameplay.gameStateHolder.applyGameStateEvents(
      game.queues.localEvents.toList.filter {
        case event: AreaGameStateEvent    => event.areaId == areaId
        case _: OperationalGameStateEvent => true
        case _                            => false
      }
    )

    game.queues.broadcastEvents.clear()
    game.queues.localEvents.clear()
  }
}
