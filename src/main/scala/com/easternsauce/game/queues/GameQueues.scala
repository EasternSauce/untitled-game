package com.easternsauce.game.queues

import com.easternsauce.game.entitycreator.AbilityToCreate
import com.easternsauce.game.entitycreator.EffectComponentToCreate
import com.easternsauce.game.entitycreator.EnemyToCreate
import com.easternsauce.game.entitycreator.PlayerToCreate
import com.easternsauce.game.entitycreator.ProjectileComponentToCreate
import com.easternsauce.game.gamephysics.PhysicsEvent
import com.easternsauce.game.gamestate.ability.scenario.AbilityScenarioEvent
import com.easternsauce.game.gamestate.event.GameStateEvent

case class Queue[T](buffer: ConcurrentListBuffer[T] = ConcurrentListBuffer()) {
  def enqueue(item: T): Unit = buffer += item
  def enqueueAll(items: IterableOnce[T]): Unit = buffer ++= items
  def drain(): List[T] = {
    val items = buffer.toList
    buffer.clear()
    items
  }
  def isEmpty: Boolean = buffer.toList.isEmpty
}

object Queue {
  def apply[T](): Queue[T] = Queue(ConcurrentListBuffer())
}

case class GameQueues(
    playerQueue: Queue[PlayerToCreate] = Queue(),
    enemyQueue: Queue[EnemyToCreate] = Queue(),
    abilityQueue: Queue[AbilityToCreate] = Queue(),
    projectileComponentQueue: Queue[ProjectileComponentToCreate] = Queue(),
    effectComponentQueue: Queue[EffectComponentToCreate] = Queue(),
    broadcastEventQueue: Queue[GameStateEvent] = Queue(),
    localEventQueue: Queue[GameStateEvent] = Queue(),
    physicsEventQueue: Queue[PhysicsEvent] = Queue(),
    abilityScenarioEventQueue: Queue[AbilityScenarioEvent] = Queue()
)
