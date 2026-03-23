package com.easternsauce.game.gamestate

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.entitycreator.GameEntityCreators
import com.easternsauce.game.gamestate.ability.Ability
import com.easternsauce.game.gamestate.ability.AbilityState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.effect.EffectComponent
import com.easternsauce.game.gamestate.event.GameStateEvent
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.gamestate.projectile.ProjectileComponent
import com.easternsauce.game.spawnpoint.SpawnPoint
import com.easternsauce.game.spawnpoint.SpawnPointUpdater
import com.easternsauce.game.util.TransformIf
import com.softwaremill.quicklens.ModifyPimp
import com.softwaremill.quicklens.QuicklensMapAt

case class GameState(
    creatures: Map[GameEntityId[Creature], Creature] = Map(),
    abilities: Map[GameEntityId[Ability], Ability] = Map(),
    projectileComponents: Map[GameEntityId[ProjectileComponent], ProjectileComponent] = Map(),
    effectComponents: Map[GameEntityId[EffectComponent], EffectComponent] = Map(),
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
      .updateCreatures(areaId, delta)
      .createEntities()
      .updateSpawnPoints(areaId)
  }

  def applyEvents(
      events: List[GameStateEvent]
  )(implicit game: CoreGame): GameState = {
    events.foldLeft(this) { case (gameState, event) =>
      event.applyToGameState(gameState)
    }
  }

  private def updateCreatures(
      areaId: AreaId,
      delta: Float
  )(implicit game: CoreGame): GameState = {

    val abilitiesToRemove = abilities.values
      .filter(a =>
        a.currentState == AbilityState.Cancelled ||
          (a.currentState == AbilityState.Finished && a.currentStateTime > 0.05f)
      )
      .toList

    val abilitiesToRemoveIds = abilitiesToRemove.map(_.id).toSet

    val projectileComponentsToRemove = projectileComponents.values
      .filter(component =>
        abilitiesToRemoveIds.contains(component.abilityId) ||
          component.params.isScheduledToBeRemoved
      )
      .toList

    val updatedState =
      this
        .updateCreatures(delta, areaId)
        .updateAbilities(delta, areaId)
        .updateProjectileComponents(delta, areaId)
        // remove components first
        .modify(_.projectileComponents)
        .using(_.removedAll(projectileComponentsToRemove.map(_.id)))
        // remove abilities AFTER delay
        .modify(_.abilities)
        .using(_.removedAll(abilitiesToRemoveIds))

    // mark finished after component removal
    val affectedAbilities =
      projectileComponentsToRemove.map(_.abilityId).toSet

    affectedAbilities.foldLeft(updatedState) { (state, abilityId) =>
      state.markAbilityAsFinishedIfNoComponentsExist(abilityId)
    }
  }

  def markAbilityAsFinishedIfNoComponentsExist(
      abilityId: GameEntityId[Ability]
  ): GameState = {

    this.transformIf(
      abilities.contains(abilityId) &&
        abilities(abilityId).finishWhenComponentsDestroyed &&
        !projectileComponents.values.exists(_.params.abilityId == abilityId)
    ) {
      this
        .modify(_.abilities.at(abilityId))
        .using(
          _.modify(_.params.state)
            .setTo(AbilityState.Finished)
            .modify(_.params.stateTimer)
            .using(_.restart()) // ✅ needed for delay
        )
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
            game.gameplay.worldSimulation.creatureBodyPositions.get(creature.id)
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

  private def updateProjectileComponents(delta: Float, areaId: AreaId)(implicit
      game: CoreGame
  ): GameState = {
    this
      .modify(_.projectileComponents.each)
      .using(projectileComponent =>
        projectileComponent.transformIf(projectileComponent.currentAreaId == areaId) {
          projectileComponent.update(
            delta,
            game.gameplay.worldSimulation.abilityBodyPositions.get(projectileComponent.id)
          )
        }
      )
  }

}
