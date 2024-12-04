package com.easternsauce.game.gamestate.event
import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.softwaremill.quicklens.{ModifyPimp, QuicklensMapAt}

case class AbilityComponentHitsCreatureEvent(
    creatureId: GameEntityId[Creature],
    abilityComponentId: GameEntityId[AbilityComponent],
    areaId: AreaId
) extends AreaGameStateEvent {

  override def applyToGameState(
      gameState: GameState
  )(implicit game: CoreGame): GameState = {
    gameState.transformIf(
      gameState.abilityComponents
        .contains(abilityComponentId) && gameState.creatures.contains(
        creatureId
      )
    ) {
      val abilityComponent = gameState.abilityComponents(abilityComponentId)
      val ability = gameState.abilities(abilityComponent.params.abilityId)
      val creature = gameState.creatures(creatureId)

      val isHitAllowed = ability.params.creatureId != creatureId &&
        (!creature.params.recentlyHitTimer.isRunning ||
          creature.params.recentlyHitTimer.time > Constants.InvulnerabilityFramesTime)

      gameState.transformIf(isHitAllowed) {
        gameState
          .modify(_.creatures.at(creatureId))
          .using(creature =>
            creature
              .modify(_.params.life)
              .using(life => {
                val lifeAfter = life - abilityComponent.params.damage
                if (lifeAfter <= 0) {
                  0
                } else {
                  lifeAfter
                }
              })
              .modify(_.params.deathAnimationTimer)
              .using(_.restart())
              .modify(_.params.recentlyHitTimer)
              .using(_.restart())
          )
          .modify(_.abilityComponents)
          .usingIf(creature.isAlive && abilityComponent.isDestroyedOnContact)(
            _.removed(abilityComponentId)
          )
          .markAbilityAsFinishedIfNoComponentsExist(abilityComponent.abilityId)
      }
    }
  }
}
