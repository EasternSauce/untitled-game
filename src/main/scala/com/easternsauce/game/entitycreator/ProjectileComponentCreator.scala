package com.easternsauce.game.entitycreator

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.ArrowComponent
import com.easternsauce.game.gamestate.ability.ExplosionComponent
import com.easternsauce.game.gamestate.ability.GhostArrowComponent
import com.easternsauce.game.gamestate.ability.ReturningArrowComponent
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.gamestate.projectile.ProjectileComponent
import com.easternsauce.game.gamestate.projectile.ProjectileComponentParams
import com.easternsauce.game.gamestate.projectile.ProjectileComponentType
import com.softwaremill.quicklens.ModifyPimp

trait ProjectileComponentCreator {
  this: GameState =>

  private[entitycreator] def createProjectileComponents(implicit
      game: CoreGame
  ): GameState = {

    val componentsToCreate = game.queues.projectileComponentQueue.drain()

    val result = componentsToCreate.foldLeft(this) {
      case (gameState, projectileComponentToCreate: ProjectileComponentToCreate) =>
        val projectileComponentId =
          GameEntityId[ProjectileComponent](
            "projectile" + (Math.random() * 1000000).toInt
          )

        val ability = gameState.abilities(projectileComponentToCreate.abilityId)

        val params = ProjectileComponentParams(
          id = projectileComponentId,
          abilityId = projectileComponentToCreate.abilityId,
          currentAreaId = projectileComponentToCreate.currentAreaId,
          creatureId = projectileComponentToCreate.creatureId,
          pos = projectileComponentToCreate.pos,
          facingVector = projectileComponentToCreate.facingVector,
          damage = projectileComponentToCreate.damage,
          scenarioStepNo = projectileComponentToCreate.scenarioStepNo,
          expirationTime = projectileComponentToCreate.expirationTime,
          spawnPos = projectileComponentToCreate.pos // NEW
        )

        val projectileComponent: ProjectileComponent =
          projectileComponentToCreate.componentType match {
            case ProjectileComponentType.ArrowComponent          => ArrowComponent(params)
            case ProjectileComponentType.GhostArrowComponent     => GhostArrowComponent(params)
            case ProjectileComponentType.ExplosionComponent      => ExplosionComponent(params)
            case ProjectileComponentType.ReturningArrowComponent => ReturningArrowComponent(params)
            case other =>
              throw new RuntimeException(s"Incorrect ability component type: $other")
          }

        gameState
          .modify(_.projectileComponents)
          .using(_.updated(projectileComponentId, projectileComponent.init()))
    }

    result
  }
}
