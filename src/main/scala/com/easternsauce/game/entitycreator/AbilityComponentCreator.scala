package com.easternsauce.game.entitycreator

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.AbilityComponentType.AbilityComponentType
import com.easternsauce.game.gamestate.ability._
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f
import com.softwaremill.quicklens.ModifyPimp

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class AbilityComponentCreator() extends EntityCreator {
  private var abilityComponentsToCreate
      : mutable.ListBuffer[AbilityComponentToCreate] = _

  override def init(): Unit = {
    abilityComponentsToCreate = ListBuffer()
  }

  override def createEntities(implicit
      game: CoreGame
  ): GameState => GameState = { gameState =>
    val result = abilityComponentsToCreate.foldLeft(gameState) {
      case (gameState, abilityComponentToCreate) =>
        val abilityComponentId =
          GameEntityId[AbilityComponent](
            "component" + (Math.random() * 1000000).toInt
          )

        val ability = gameState.abilities(abilityComponentToCreate.abilityId)

        val params = AbilityComponentParams(
          id = abilityComponentId,
          abilityId = abilityComponentToCreate.abilityId,
          currentAreaId = abilityComponentToCreate.currentAreaId,
          creatureId = abilityComponentToCreate.creatureId,
          pos = abilityComponentToCreate.pos,
          facingVector = abilityComponentToCreate.facingVector,
          damage = abilityComponentToCreate.damage,
          velocity = abilityComponentToCreate.facingVector.normalized.multiply(ability.params.speed)
        )
        val abilityComponent =
          if (
            abilityComponentToCreate.componentType == AbilityComponentType.ArrowComponent
          ) { ArrowComponent(params) }
          else {
            throw new RuntimeException("incorrect ability component type")
          }

        gameState
          .modify(_.abilityComponents)
          .using(_.updated(abilityComponentId, abilityComponent.init()))
    }

    abilityComponentsToCreate.clear()

    result
  }

  def scheduleAbilityComponentToCreate(
      abilityId: GameEntityId[Ability],
      componentType: AbilityComponentType,
      currentAreaId: AreaId,
      creatureId: GameEntityId[Creature],
      pos: Vector2f,
      facingVector: Vector2f,
      damage: Float
  ): Unit = {
    abilityComponentsToCreate += AbilityComponentToCreate(
      abilityId,
      componentType,
      currentAreaId,
      creatureId,
      pos,
      facingVector,
      damage
    )
  }

  private case class AbilityComponentToCreate(
      abilityId: GameEntityId[Ability],
      componentType: AbilityComponentType,
      currentAreaId: AreaId,
      creatureId: GameEntityId[Creature],
      pos: Vector2f,
      facingVector: Vector2f,
      damage: Float
  )

}
