package com.easternsauce.game.gamestate.event
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.entitycreator.ProjectileComponentToCreate
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.AbilityState
import com.easternsauce.game.gamestate.ability.ArrowComponent
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.gamestate.projectile.ProjectileComponent
import com.easternsauce.game.gamestate.projectile.ProjectileComponentType
import com.softwaremill.quicklens.ModifyPimp
import com.softwaremill.quicklens.QuicklensMapAt

case class ProjectileComponentHitsTerrainEvent(
    projectileComponentId: GameEntityId[ProjectileComponent],
    areaId: AreaId
) extends AreaGameStateEvent {

  override def applyToGameState(
      gameState: GameState
  )(implicit game: CoreGame): GameState = {

    if (!gameState.projectileComponents.contains(projectileComponentId)) {
      return gameState
    }

    val component = gameState.projectileComponents(projectileComponentId)

    if (!gameState.abilities.contains(component.abilityId)) {
      return gameState
    }

    val ability = game.gameState.abilities(component.abilityId)

    if (ability.currentState != AbilityState.Active) {
      return gameState
    }

    // ✅ Arrow: destroy + spawn returning
    if (component.isInstanceOf[ArrowComponent]) {

      game.queues.projectileComponentQueue.enqueue(
        ProjectileComponentToCreate(
          abilityId = component.abilityId,
          componentType = ProjectileComponentType.ReturningArrowComponent,
          currentAreaId = component.currentAreaId,
          creatureId = component.params.creatureId,
          pos = component.pos,
          facingVector = component.params.facingVector.multiply(-1f),
          damage = component.params.damage,
          scenarioStepNo = component.params.scenarioStepNo,
          expirationTime = None
        )
      )

      return gameState
        .modify(_.projectileComponents.at(component.id).params.isScheduledToBeRemoved)
        .setTo(true)
        .modify(_.projectileComponents.at(component.id).params.isContinueScenario)
        .setTo(false)
    }

    gameState
      .modify(
        _.projectileComponents
          .at(component.id)
          .params
          .isScheduledToBeRemoved
      )
      .setToIf(component.isDestroyedOnTerrainContact)(true)
      .modify(
        _.projectileComponents
          .at(component.id)
          .params
          .isContinueScenario
      )
      .setToIf(component.isDestroyedOnTerrainContact)(false)
  }
}
