package com.easternsauce.game.gamestate.event
import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.entitycreator.ProjectileComponentToCreate
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.ArrowComponent
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.gamestate.projectile.ProjectileComponent
import com.easternsauce.game.gamestate.projectile.ProjectileComponentType
import com.softwaremill.quicklens.ModifyPimp
import com.softwaremill.quicklens.QuicklensMapAt

case class ProjectileComponentHitsCreatureEvent(
    creatureId: GameEntityId[Creature],
    projectileComponentId: GameEntityId[ProjectileComponent],
    areaId: AreaId
) extends AreaGameStateEvent {

  override def applyToGameState(
      gameState: GameState
  )(implicit game: CoreGame): GameState = {

    gameState.transformIf(
      gameState.projectileComponents.contains(projectileComponentId) &&
        gameState.creatures.contains(creatureId)
    ) {

      val projectileComponent = gameState.projectileComponents(projectileComponentId)

      val abilityOpt = gameState.abilities.get(projectileComponent.params.abilityId)
      if (abilityOpt.isEmpty) {
        return gameState
      }
      val ability = abilityOpt.get

      val creature = gameState.creatures(creatureId)

      val isHitAllowed =
        ability.params.creatureId != creatureId && creature.isAlive &&
          (!creature.params.recentlyHitTimer.isRunning ||
            creature.params.recentlyHitTimer.time > Constants.InvulnerabilityFramesTime)

      gameState.transformIf(isHitAllowed) {

        val lifeAfterHit =
          Math.max(0, creature.params.life - projectileComponent.params.damage)

        val isHitFatal = lifeAfterHit <= 0

        val newPierceCount = projectileComponent.params.piercedTargets + 1

        val reachedPierceLimit =
          projectileComponent match {
            case arrow: ArrowComponent =>
              newPierceCount >= arrow.maxPierce
            case _ => false
          }

        // ✅ spawn returning arrow if limit reached
        if (reachedPierceLimit) {
          game.queues.projectileComponentQueue.enqueue(
            ProjectileComponentToCreate(
              abilityId = projectileComponent.abilityId,
              componentType = ProjectileComponentType.ReturningArrowComponent,
              currentAreaId = projectileComponent.currentAreaId,
              creatureId = projectileComponent.params.creatureId,
              pos = projectileComponent.pos,
              facingVector = projectileComponent.params.facingVector.multiply(-1f),
              damage = projectileComponent.params.damage,
              scenarioStepNo = projectileComponent.params.scenarioStepNo,
              expirationTime = None
            )
          )
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
          .modify(_.projectileComponents.at(projectileComponentId))
          .using { component =>
            if (reachedPierceLimit) {
              component
                .modify(_.params.isScheduledToBeRemoved)
                .setTo(true)
            } else {
              component
                .modify(_.params.piercedTargets)
                .setTo(newPierceCount)
            }
          }
      }
    }
  }
}
