package com.easternsauce.game.core

import com.easternsauce.game.Constants
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.{Ability, AbilityComponent, AbilityParams, Arrow}
import com.easternsauce.game.gamestate.creature.{Creature, CreatureType}
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f
import com.softwaremill.quicklens.ModifyPimp

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class EntityCreator() {
  private var abilityComponentsToCreate: mutable.ListBuffer[AbilityComponent] =
    _
  private var playersToCreate: mutable.ListBuffer[String] = _

  def init(): Unit = {
    abilityComponentsToCreate = ListBuffer()
    playersToCreate = ListBuffer()
  }

  def scheduleAbilityComponentToCreate(
      abilityComponent: AbilityComponent
  ): Unit = {
    abilityComponentsToCreate += abilityComponent
  }

  def schedulePlayerToCreate(clientId: String): Unit = {
    playersToCreate += clientId
  }

  def createScheduledEntities()(implicit
      game: CoreGame
  ): GameState => GameState = {
    createScheduledPlayers.andThen(createScheduledAbilityComponents)
  }

  private def createScheduledPlayers(implicit
      game: CoreGame
  ): GameState => GameState = { gameState =>
    playersToCreate.clear()

    playersToCreate.foldLeft(gameState) { case (gameState, name) =>
      val creatureId = GameEntityId[Creature](name)

      val abilityId = GameEntityId[Ability]("meh1")

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
              Arrow(
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

  private def createScheduledAbilityComponents(implicit
      game: CoreGame
  ): GameState => GameState = { gameState =>
    abilityComponentsToCreate.clear()

    abilityComponentsToCreate.foldLeft(gameState) {
      case (gameState, abilityComponent) =>
        ???
        gameState
    }
  }
}
