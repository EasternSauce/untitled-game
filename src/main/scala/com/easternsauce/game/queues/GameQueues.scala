package com.easternsauce.game.queues

import com.easternsauce.game.entitycreator.{AbilityComponentToCreate, AbilityToCreate, PlayerToCreate}
import com.easternsauce.game.gamestate.event.GameStateEvent

case class GameQueues(
    playersToCreate: ConcurrentListBuffer[PlayerToCreate] =
      ConcurrentListBuffer(),
    abilitiesToCreate: ConcurrentListBuffer[AbilityToCreate] =
      ConcurrentListBuffer(),
    abilityComponentsToCreate: ConcurrentListBuffer[AbilityComponentToCreate] =
      ConcurrentListBuffer(),
    broadcastEvents: ConcurrentListBuffer[GameStateEvent] =
      ConcurrentListBuffer(),
    localEvents: ConcurrentListBuffer[GameStateEvent] = ConcurrentListBuffer()
)
