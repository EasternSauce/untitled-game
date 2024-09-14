package com.easternsauce.game.gamestate

import com.easternsauce.game.gamestate.creature.{Creature, CreatureFactory}
import com.easternsauce.game.gamestate.event.GameStateEvent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f
import com.easternsauce.game.{Constants, CoreGame}
import com.softwaremill.quicklens.{ModifyPimp, QuicklensMapAt}

case class GameState(
    creatures: Map[GameEntityId[Creature], Creature] = Map(),
    activeCreatureIds: Set[GameEntityId[Creature]] = Set()
) {
  def updateForArea(areaId: AreaId, delta: Float)(implicit
      game: CoreGame
  ): GameState = {
    val updatedGameState = this
      .updateCreaturesForArea(areaId, delta)
      .handleCreatePlayers() // TODO: server only?

    game.processBroadcastEventsForArea(areaId, updatedGameState)
  }

  def applyEvents(events: List[GameStateEvent]): GameState = {
    events.foldLeft(this) { case (gameState, event) =>
      event.applyToGameState(gameState)
    }
  }

  private def updateCreaturesForArea(
      areaId: AreaId,
      delta: Float
  )(implicit game: CoreGame): GameState = {
    this
      .modify(_.creatures.each)
      .using(creature =>
        if (
          activeCreatureIds
            .contains(creature.id) && creature.currentAreaId == areaId
        ) {
          creature.update(
            delta,
            game.physics.creatureBodyPositions.get(creature.id)
          )
        } else {
          creature
        }
      )
  }

  private def handleCreatePlayers()(implicit game: CoreGame): GameState = {
    val playersToCreate = game.playersToCreate

    game.clearPlayersToCreate()

    playersToCreate.foldLeft(this) { case (gameState, name) =>
      val creatureId = GameEntityId[Creature](name)

      if (gameState.creatures.contains(creatureId)) {
        gameState
          .modify(_.activeCreatureIds)
          .using(_ + creatureId)
      } else {
        gameState
          .modify(_.creatures)
          .using(
            _.updated(
              creatureId,
              CreatureFactory
                .male1(
                  creatureId,
                  Constants.DefaultAreaId,
                  Vector2f(
                    5f + 3f * Math.random().toFloat,
                    415f + 3f * Math.random().toFloat
                  ),
                  player = true,
                  8f
                )
            )
          )
          .modify(_.activeCreatureIds)
          .using(_ + creatureId)
      }
    }
  }
}
