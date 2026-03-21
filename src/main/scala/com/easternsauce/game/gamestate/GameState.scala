package com.easternsauce.game.gamestate

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.entitycreator.GameEntityCreators
import com.easternsauce.game.gamestate.ability.Ability
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.ability.AbilityState
import com.easternsauce.game.gamestate.ability.scenario.AbilityComponentScenarioRunStepEvent
import com.easternsauce.game.gamestate.ability.scenario.AbilityComponentScenarioStepParams
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.event.GameStateEvent
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.spawnpoint.SpawnPoint
import com.easternsauce.game.spawnpoint.SpawnPointUpdater
import com.easternsauce.game.util.TransformIf
import com.softwaremill.quicklens.ModifyPimp
import com.softwaremill.quicklens.QuicklensMapAt

case class GameState(
    creatures: Map[GameEntityId[Creature], Creature] = Map(),
    abilities: Map[GameEntityId[Ability], Ability] = Map(),
    abilityComponents: Map[GameEntityId[AbilityComponent], AbilityComponent] = Map(),
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
      .filter(ability =>
        ability.currentState == AbilityState.Cancelled || ability.currentState == AbilityState.Finished
      )
      .toList

    val abilityComponentsToRemove = abilityComponents.values
      .filter(component =>
        abilitiesToRemove.contains(component.abilityId) || component.params.isScheduledToBeRemoved
      )
      .toList

    if (abilityComponentsToRemove.nonEmpty) {
      println(s"[DEBUG] Preparing to remove ${abilityComponentsToRemove.size} components this frame")
    }

    abilityComponentsToRemove.foreach { component =>
      println(
        s"[DEBUG] Removing component ${component.id} of ability ${component.params.abilityId}, isContinueScenario=${component.params.isContinueScenario}"
      )
    }

    abilityComponentsToRemove
      .filter(_.params.isContinueScenario)
      .foreach { component =>
        println(s"[DEBUG] Enqueuing next scenario step for component ${component.id}")
        game.queues.abilityScenarioEventQueue.enqueue(
          AbilityComponentScenarioRunStepEvent(
            AbilityComponentScenarioStepParams(
              component.abilityId,
              component.currentAreaId,
              component.params.creatureId,
              component.pos,
              component.params.facingVector,
              component.params.damage,
              component.params.scenarioStepNo + 1
            )
          )
        )
      }

    val updatedState =
      this
        .updateCreatures(delta, areaId)
        .updateAbilities(delta, areaId)
        .updateAbilityComponents(delta, areaId)
        .modify(_.abilities)
        .using(_.removedAll(abilitiesToRemove.map(_.id)))
        .modify(_.abilityComponents)
        .using(_.removedAll(abilityComponentsToRemove.map(_.id)))

    // ✅ NEW: always check finish after removals
    val affectedAbilities =
      abilityComponentsToRemove.map(_.abilityId).toSet

    affectedAbilities.foldLeft(updatedState) { (state, abilityId) =>
      println(s"[DEBUG] Post-removal check for ability $abilityId")
      state.markAbilityAsFinishedIfNoComponentsExist(abilityId)
    }
  }

  def markAbilityAsFinishedIfNoComponentsExist(
                                                abilityId: GameEntityId[Ability]
                                              ): GameState = {
    println(s"[DEBUG] Checking if ability $abilityId should be finished. Components remaining: " +
      s"${abilityComponents.values.count(_.params.abilityId == abilityId)}, finishWhenComponentsDestroyed: " +
      s"${abilities.get(abilityId).map(_.finishWhenComponentsDestroyed).getOrElse(false)}"
    )

    this.transformIf(
      abilities.contains(abilityId) && abilities(
        abilityId
      ).finishWhenComponentsDestroyed &&
        !abilityComponents.values
          .exists(_.params.abilityId == abilityId)
    ) {
      println(s"[DEBUG] Ability $abilityId has no remaining components and is now FINISHED")
      this.modify(_.abilities.at(abilityId))
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

  private def updateAbilityComponents(delta: Float, areaId: AreaId)(implicit
      game: CoreGame
  ): GameState = {
    this
      .modify(_.abilityComponents.each)
      .using(abilityComponent =>
        abilityComponent.transformIf(abilityComponent.currentAreaId == areaId) {
          abilityComponent.update(
            delta,
            game.gameplay.worldSimulation.abilityBodyPositions.get(abilityComponent.id)
          )
        }
      )
  }

}
