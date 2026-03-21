package com.easternsauce.game.gamestate.event
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.entitycreator.AbilityComponentToCreate
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.ability.AbilityComponentType
import com.easternsauce.game.gamestate.ability.AbilityState
import com.easternsauce.game.gamestate.ability.ArrowComponent
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.softwaremill.quicklens.ModifyPimp
import com.softwaremill.quicklens.QuicklensMapAt

case class AbilityComponentHitsTerrainEvent(
    abilityComponentId: GameEntityId[AbilityComponent],
    areaId: AreaId
) extends AreaGameStateEvent {

  override def applyToGameState(
      gameState: GameState
  )(implicit game: CoreGame): GameState = {

    if (!gameState.abilityComponents.contains(abilityComponentId)) {
      return gameState
    }

    val component = gameState.abilityComponents(abilityComponentId)

    if (!gameState.abilities.contains(component.abilityId)) {
      return gameState
    }

    val ability = game.gameState.abilities(component.abilityId)

    if (ability.currentState != AbilityState.Active) {
      return gameState
    }

    // 🔥 NEW: spawn returning arrow ONLY for ArrowComponent
    if (component.isInstanceOf[ArrowComponent]) {
      game.queues.abilityComponentQueue.enqueue(
        AbilityComponentToCreate(
          abilityId = component.abilityId,
          componentType = AbilityComponentType.ReturningArrowComponent,
          currentAreaId = component.currentAreaId,
          creatureId = component.params.creatureId,
          pos = component.pos,
          facingVector = component.params.facingVector,
          damage = component.params.damage,
          scenarioStepNo = component.params.scenarioStepNo,
          expirationTime = None
        )
      )
    }

    gameState
      .modify(
        _.abilityComponents
          .at(component.id)
          .params
          .isScheduledToBeRemoved
      )
      .setToIf(component.isDestroyedOnTerrainContact)(true)
      .modify(
        _.abilityComponents
          .at(component.id)
          .params
          .isContinueScenario
      )
      .setToIf(component.isDestroyedOnTerrainContact)(false)
  }
}
