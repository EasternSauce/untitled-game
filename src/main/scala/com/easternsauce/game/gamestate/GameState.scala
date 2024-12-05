package com.easternsauce.game.gamestate

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.entitycreator.GameEntityCreators
import com.easternsauce.game.gamestate.ability.{Ability, AbilityComponent, AbilityState}
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.event.GameStateEvent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.spawnpoint.{SpawnPoint, SpawnPointUpdater}
import com.easternsauce.game.util.TransformIf
import com.softwaremill.quicklens.{ModifyPimp, QuicklensMapAt}

case class GameState(
    creatures: Map[GameEntityId[Creature], Creature] = Map(),
    abilities: Map[GameEntityId[Ability], Ability] = Map(),
    abilityComponents: Map[GameEntityId[AbilityComponent], AbilityComponent] =
      Map(),
    activePlayerIds: Set[GameEntityId[Creature]] = Set(),
    spawnPoints: Map[String, SpawnPoint] = Map(),
    mainTimer: SimpleTimer = SimpleTimer(isRunning = true)
) extends TransformIf
    with GameEntityCreators
    with SpawnPointUpdater {
  def init(): GameState = {
    val spawnPoints = Constants.MapAreaSpawnPoints

    spawnPoints.foldLeft(this)((gameState, spawnPoint) =>
      gameState
        .modify(_.spawnPoints)
        .using(_.updated(spawnPoint.id, spawnPoint))
    )
  }

  def updateTimers(delta: Float): GameState = {
    this.modify(_.mainTimer).using(_.update(delta))
  }

  def update(areaId: AreaId, delta: Float)(implicit
      game: CoreGame
  ): GameState = {
    this
      .updateCreaturesForArea(areaId, delta)
      .createEntities
      .updateSpawnPoints(areaId)
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
    this.transformIf(
      !abilityComponents.values
        .exists(_.params.abilityId == abilityId)
    ) {
      this
        .modify(_.abilities.at(abilityId))
        .using(_.modify(_.params.state).setTo(AbilityState.Finished))
    }
  }

  private def updateCreatures(delta: Float, areaId: AreaId)(implicit
      game: CoreGame
  ): GameState = {
    this
      .modify(_.creatures.each)
      .using(creature =>
        creature.transformIf(
          creature.currentAreaId == areaId && activePlayerIds
            .contains(creature.id) || !creature.params.isPlayer
        ) {
          creature.update(
            delta,
            game.gameplay.physics.creatureBodyPositions.get(creature.id)
          )
        }
      )
  }
  private def updateAbilities(delta: Float, areaId: AreaId)(implicit
      game: CoreGame
  ): GameState = {
    this
      .modify(_.abilities.each)
      .using(ability =>
        ability.transformIf(ability.currentAreaId == areaId) {
          ability.update(delta)
        }
      )
  }

  private def updateAbilityComponents(delta: Float, areaId: AreaId)(implicit
      game: CoreGame
  ): GameState = {
    this
      .modify(_.abilityComponents.each)
      .using(abilityComponent =>
        abilityComponent.transformIf(abilityComponent.currentAreaId == areaId) {
          abilityComponent.update(
            delta,
            game.gameplay.physics.abilityBodyPositions.get(abilityComponent.id)
          )
        }
      )
  }
}
