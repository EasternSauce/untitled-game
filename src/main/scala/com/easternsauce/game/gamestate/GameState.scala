package com.easternsauce.game.gamestate

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.{Ability, AbilityComponent, AbilityParams}
import com.easternsauce.game.gamestate.creature.{Creature, CreatureType}
import com.easternsauce.game.gamestate.event.GameStateEvent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f
import com.softwaremill.quicklens.{ModifyPimp, QuicklensMapAt}

case class GameState(
    creatures: Map[GameEntityId[Creature], Creature] = Map(),
    abilities: Map[GameEntityId[Ability], Ability] = Map(),
    activeCreatureIds: Set[GameEntityId[Creature]] = Set()
) {
  def updateForArea(areaId: AreaId, delta: Float)(implicit
      game: CoreGame
  ): GameState = {
    this
      .updateCreaturesForArea(areaId, delta)
      .handleCreatePlayers() // TODO: server only?

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
            game.gameplay.physics.creatureBodyPositions.get(creature.id)
          )
        } else {
          creature
        }
      )
      .modify(_.abilities.each)
      .using(ability =>
        if (ability.currentAreaId == areaId) {
          ability.update(
            delta,
            game.gameplay.physics.abilityBodyPositions.get(ability.id)
          )
        } else {
          ability
        }
      )
  }

  private def handleCreatePlayers()(implicit game: CoreGame): GameState = {
    val playersToCreate = game.gameplay.playersToCreateScheduler.playersToCreate

    game.gameplay.playersToCreateScheduler.clearPlayersToCreate()

    playersToCreate.foldLeft(this) { case (gameState, name) =>
      val creatureId = GameEntityId[Creature](name)

      val abilityId = GameEntityId[AbilityComponent]("meh")

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
              Creature.produce(
                creatureId,
                Constants.DefaultAreaId,
                Vector2f(
                  5f,
                  415f
                ),
                player = true,
                creatureType = CreatureType.Human
              )
            )
          )
          .modify(_.activeCreatureIds)
          .using(_ + creatureId)
          .modify(_.abilities)
          .using(
            _.updated(
              abilityId,
              ArrowComponent(
                AbilityParams(
                  id = abilityId,
                  currentAreaId = Constants.DefaultAreaId,
                  creatureId = creatureId,
                  pos = Vector2f(
                    5f,
                    418f
                  ),
                  damage = 0f
                )
              )
            )
          )
      }
    }
  }
}
