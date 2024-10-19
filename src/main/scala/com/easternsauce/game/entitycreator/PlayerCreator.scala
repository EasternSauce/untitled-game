package com.easternsauce.game.entitycreator

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.{Ability, AbilityParams, Arrow}
import com.easternsauce.game.gamestate.creature.{Creature, CreatureType}
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f
import com.softwaremill.quicklens.ModifyPimp

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class PlayerCreator() extends EntityCreator {
  private var playersToCreate: mutable.ListBuffer[String] = _

  override def init(): Unit = {
    playersToCreate = ListBuffer()
  }

  override def createEntities(implicit
      game: CoreGame
  ): GameState => GameState = { gameState =>
    val result = playersToCreate.foldLeft(gameState) { case (gameState, name) =>
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

    playersToCreate.clear()

    result
  }

  def schedulePlayerToCreate(clientId: String): Unit = {
    println("scheduling...")
    playersToCreate += clientId
  }

}
