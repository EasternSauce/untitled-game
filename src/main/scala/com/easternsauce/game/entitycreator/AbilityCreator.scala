package com.easternsauce.game.entitycreator

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.AbilityType.AbilityType
import com.easternsauce.game.gamestate.ability.{Ability, AbilityParams, AbilityType, Arrow}
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f
import com.softwaremill.quicklens.ModifyPimp

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class AbilityCreator() extends EntityCreator {
  private var abilitiesToCreate: mutable.ListBuffer[AbilityToCreate] =
    _

  override def init(): Unit = {
    abilitiesToCreate = ListBuffer()
  }

  override def createEntities(implicit
      game: CoreGame
  ): GameState => GameState = { gameState =>
    val result = abilitiesToCreate.foldLeft(gameState) {
      case (gameState, abilityToCreate) =>
        val abilityId =
          GameEntityId[Ability](
            "ability" + (Math.random() * 1000000).toInt
          )

        val params = AbilityParams(
          id = abilityId,
          currentAreaId = abilityToCreate.currentAreaId,
          creatureId = abilityToCreate.creatureId,
          pos = abilityToCreate.pos,
          facingVector = abilityToCreate.facingVector
        )
        val ability =
          if (abilityToCreate.abilityType == AbilityType.Arrow) {
            Arrow(params)
          } else {
            throw new RuntimeException("incorrect ability component type")
          }

        gameState
          .modify(_.abilities)
          .using(_.updated(abilityId, ability.init()))
    }

    abilitiesToCreate.clear()

    result
  }

  def scheduleAbilityToCreate(
      abilityType: AbilityType,
      currentAreaId: AreaId,
      creatureId: GameEntityId[Creature],
      pos: Vector2f,
      facingVector: Vector2f
  ): Unit = {
    abilitiesToCreate += AbilityToCreate(
      abilityType,
      currentAreaId,
      creatureId,
      pos,
      facingVector
    )
  }

  private case class AbilityToCreate(
      abilityType: AbilityType,
      currentAreaId: AreaId,
      creatureId: GameEntityId[Creature],
      pos: Vector2f,
      facingVector: Vector2f
  )

}
