package com.easternsauce.game.gamestate.event
import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamephysics.MakeBodySensorEvent
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
      gameState.abilityComponents.contains(abilityComponentId) &&
        gameState.creatures.contains(creatureId)
    ) {
      val abilityComponent = gameState.abilityComponents(abilityComponentId)
      val ability = gameState.abilities(abilityComponent.params.abilityId)
      val creature = gameState.creatures(creatureId)

      val isHitAllowed =
        ability.params.creatureId != creatureId && creature.isAlive &&
          (!creature.params.recentlyHitTimer.isRunning ||
            creature.params.recentlyHitTimer.time > Constants.InvulnerabilityFramesTime)

      gameState.transformIf(isHitAllowed) {
        val lifeAfterHit =
          if (creature.params.life - abilityComponent.params.damage <= 0) {
            0
          } else {
            creature.params.life - abilityComponent.params.damage
          }

        val isHitFatal = lifeAfterHit <= 0

        if (isHitFatal) {
          game.queues.physicsEvents += MakeBodySensorEvent(creatureId)
        }

        gameState
          .modify(_.creatures.at(creatureId))
          .using(creature =>
            creature
              .modify(_.params.life)
              .setTo(lifeAfterHit)
              .modify(_.params.recentlyHitTimer)
              .using(_.restart())
              .modify(_.params.deathAnimationTimer)
              .usingIf(isHitFatal)(_.restart())
          )
          .modify(
            _.abilityComponents
              .at(abilityComponentId)
              .params
              .isScheduledToBeRemoved
          )
          .setToIf(
            creature.isAlive && abilityComponent.isDestroyedOnCreatureContact
          )(true)
          .markAbilityAsFinishedIfNoComponentsExist(abilityComponent.abilityId)
      }
    }
  }
}
