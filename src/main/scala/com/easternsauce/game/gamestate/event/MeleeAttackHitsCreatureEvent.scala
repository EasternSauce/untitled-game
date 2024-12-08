package com.easternsauce.game.gamestate.event
import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamephysics.MakeBodySensorEvent
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.Ability
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.softwaremill.quicklens.{ModifyPimp, QuicklensMapAt}

case class MeleeAttackHitsCreatureEvent(
    creatureId: GameEntityId[Creature],
    abilityId: GameEntityId[Ability],
    areaId: AreaId
) extends AreaGameStateEvent {

  override def applyToGameState(
      gameState: GameState
  )(implicit game: CoreGame): GameState = {
    gameState.transformIf(
      gameState.abilities
        .contains(abilityId) && gameState.creatures.contains(
        creatureId
      )
    ) {
      val ability = gameState.abilities(abilityId)
      val creature = gameState.creatures(creatureId)

      val isHitAllowed =
        ability.params.creatureId != creatureId && creature.isAlive &&
          (!creature.params.recentlyHitTimer.isRunning ||
            creature.params.recentlyHitTimer.time > Constants.InvulnerabilityFramesTime)

      gameState.transformIf(isHitAllowed) {
        val lifeAfterHit =
          if (creature.params.life - ability.params.damage <= 0) { 0 }
          else {
            creature.params.life - ability.params.damage
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
      }
    }
  }
}
