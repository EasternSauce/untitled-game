package com.easternsauce.game.gamestate

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.{Ability, AbilityComponent, AbilityState}
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.event.GameStateEvent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.spawnpoint.SpawnPoint
import com.softwaremill.quicklens.{ModifyPimp, QuicklensMapAt}

import scala.util.chaining.scalaUtilChainingOps

case class GameState(
    creatures: Map[GameEntityId[Creature], Creature] = Map(),
    abilities: Map[GameEntityId[Ability], Ability] = Map(),
    abilityComponents: Map[GameEntityId[AbilityComponent], AbilityComponent] =
      Map(),
    activePlayerIds: Set[GameEntityId[Creature]] = Set(),
    spawnPoints: Map[String, SpawnPoint] = Map(),
    mainTimer: SimpleTimer = SimpleTimer(running = true)
) {

  def init(): GameState = {
    val spawnPoints = Constants.MapAreaSpawnPoints.map(SpawnPoint)

    spawnPoints.foldLeft(this)((gameState, spawnPoint) =>
      gameState
        .modify(_.spawnPoints)
        .using(_.updated(spawnPoint.spawnPointData.spawnPointId, spawnPoint))
    )
  }

  def updateTimers(delta: Float): GameState = {
    this.modify(_.mainTimer).using(_.update(delta))
  }

  def updateForArea(areaId: AreaId, delta: Float)(implicit
      game: CoreGame
  ): GameState = {
    this
      .updateCreaturesForArea(areaId, delta)
      .pipe(game.gameplay.entityCreators.createScheduledEntities)
      .updateSpawnPointsForArea(areaId)

  }

  private def updateSpawnPointsForArea(areaId: AreaId)(implicit
      game: CoreGame
  ): GameState = {
    spawnPoints.values.foldLeft(this) { case (gameState, spawnPoint) =>
      if (spawnPoint.areaId == areaId) {
        gameState.pipe(spawnPoint.update())
      } else {
        gameState
      }
    }
  }

  def applyEvents(
      events: List[GameStateEvent]
  )(implicit game: CoreGame): GameState = {
    events.foldLeft(this) { case (gameState, event) =>
      event.applyToGameState(gameState)
    }
  }

  private def updateCreaturesForArea(
      areaId: AreaId,
      delta: Float
  )(implicit game: CoreGame): GameState = {
    val abilitiesToRemove = abilities.values
      .filter(ability =>
        ability.currentState == AbilityState.Cancelled || ability.currentState == AbilityState.Finished
      )
      .map(_.id)
      .toList

    val abilityComponentsToRemove = abilityComponents.values
      .filter(component => abilitiesToRemove.contains(component.abilityId))
      .map(_.id)

    this
      .updateCreatures(delta, areaId)
      .updateAbilities(delta, areaId)
      .updateAbilityComponents(delta, areaId)
      .modify(_.abilities)
      .using(_.removedAll(abilitiesToRemove))
      .modify(_.abilityComponents)
      .using(_.removedAll(abilityComponentsToRemove))
  }

  def markAbilityAsFinishedIfNoComponentsExist(
      abilityId: GameEntityId[Ability]
  ): GameState = {
    if (
      !abilityComponents.values
        .exists(_.params.abilityId == abilityId)
    ) {
      this
        .modify(_.abilities.at(abilityId))
        .using(_.modify(_.params.state).setTo(AbilityState.Finished))
    } else {
      this
    }
  }

  private def updateCreatures(delta: Float, areaId: AreaId)(implicit
      game: CoreGame
  ): GameState = {
    this
      .modify(_.creatures.each)
      .using(creature =>
        if (
          creature.currentAreaId == areaId && activePlayerIds
            .contains(creature.id) || !creature.params.player
        ) {
          creature.update(
            delta,
            game.gameplay.physics.creatureBodyPositions.get(creature.id)
          )
        } else {
          creature
        }
      )
  }
  private def updateAbilities(delta: Float, areaId: AreaId)(implicit
      game: CoreGame
  ): GameState = {
    this
      .modify(_.abilities.each)
      .using(ability =>
        if (ability.currentAreaId == areaId) {
          ability.update(delta)
        } else {
          ability
        }
      )
  }

  private def updateAbilityComponents(delta: Float, areaId: AreaId)(implicit
      game: CoreGame
  ): GameState = {
    this
      .modify(_.abilityComponents.each)
      .using(abilityComponent =>
        if (abilityComponent.currentAreaId == areaId) {
          abilityComponent.update(
            delta,
            game.gameplay.physics.abilityBodyPositions.get(abilityComponent.id)
          )
        } else {
          abilityComponent
        }
      )
  }
}
