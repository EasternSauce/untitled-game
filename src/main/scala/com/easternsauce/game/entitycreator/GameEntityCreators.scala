package com.easternsauce.game.entitycreator

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.Ability
import com.easternsauce.game.gamestate.ability.AbilityComponentType.AbilityComponentType
import com.easternsauce.game.gamestate.ability.AbilityType.AbilityType
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

case class GameEntityCreators() {
  private var playerCreator: PlayerCreator = _
  private var abilityCreator: AbilityCreator = _
  private var abilityComponentCreator: AbilityComponentCreator = _

  def init(): Unit = {
    playerCreator = PlayerCreator()
    playerCreator.init()
    abilityCreator = AbilityCreator()
    abilityCreator.init()
    abilityComponentCreator = AbilityComponentCreator()
    abilityComponentCreator.init()
  }

  def createScheduledEntities()(implicit
      game: CoreGame
  ): GameState => GameState = {
    playerCreator.createEntities
      .andThen(abilityCreator.createEntities)
      .andThen(abilityComponentCreator.createEntities)
  }

  def schedulePlayerToCreate(clientId: String): Unit =
    playerCreator.schedulePlayerToCreate(clientId)

  def scheduleAbilityToCreate(
      abilityType: AbilityType,
      currentAreaId: AreaId,
      creatureId: GameEntityId[Creature],
      pos: Vector2f,
      facingVector: Vector2f
  ): Unit = abilityCreator.scheduleAbilityToCreate(
    abilityType,
    currentAreaId,
    creatureId,
    pos,
    facingVector
  )

  def scheduleAbilityComponentToCreate(
      abilityId: GameEntityId[Ability],
      componentType: AbilityComponentType,
      currentAreaId: AreaId,
      creatureId: GameEntityId[Creature],
      pos: Vector2f,
      facingVector: Vector2f,
      damage: Float
  ): Unit = abilityComponentCreator.scheduleAbilityComponentToCreate(
    abilityId,
    componentType,
    currentAreaId,
    creatureId,
    pos,
    facingVector,
    damage
  )
}
