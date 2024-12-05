package com.easternsauce.game.queues

import com.easternsauce.game.entitycreator.{
  AbilityComponentToCreate,
  AbilityToCreate,
  EnemyToCreate,
  PlayerToCreate
}
import com.easternsauce.game.gamephysics.PhysicsEvent
import com.easternsauce.game.gamestate.event.GameStateEvent

case class GameQueues(
    playersToCreate: ConcurrentListBuffer[PlayerToCreate] =
      ConcurrentListBuffer(),
    enemiesToCreate: ConcurrentListBuffer[EnemyToCreate] =
      ConcurrentListBuffer(),
    abilitiesToCreate: ConcurrentListBuffer[AbilityToCreate] =
      ConcurrentListBuffer(),
    abilityComponentsToCreate: ConcurrentListBuffer[AbilityComponentToCreate] =
      ConcurrentListBuffer(),
    broadcastEvents: ConcurrentListBuffer[GameStateEvent] =
      ConcurrentListBuffer(),
    localEvents: ConcurrentListBuffer[GameStateEvent] = ConcurrentListBuffer(),
    physicsEvents: ConcurrentListBuffer[PhysicsEvent] = ConcurrentListBuffer()
)
