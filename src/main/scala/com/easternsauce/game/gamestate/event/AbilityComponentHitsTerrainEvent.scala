package com.easternsauce.game.gamestate.event
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.softwaremill.quicklens.ModifyPimp

import scala.util.chaining.scalaUtilChainingOps

case class AbilityComponentHitsTerrainEvent(
    abilityComponentId: GameEntityId[AbilityComponent],
    areaId: AreaId
) extends AreaGameStateEvent {

  override def applyToGameState(
      gameState: GameState
  )(implicit game: CoreGame): GameState = {
    if (
      gameState.abilityComponents
        .contains(abilityComponentId)
    ) {
      val abilityComponent = gameState.abilityComponents(abilityComponentId)

      gameState
        .modify(_.abilityComponents)
        .usingIf(abilityComponent.destroyedOnContact)(
          _.removed(abilityComponentId)
        )
        .pipe(_.removeAbilityIfCompleted(abilityComponent.abilityId))
    } else {
      gameState
    }
  }
}
