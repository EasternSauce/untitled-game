package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.entitycreator.EffectComponentToCreate
import com.easternsauce.game.entitycreator.ProjectileComponentToCreate
import com.easternsauce.game.gamestate.GameEntity
import com.easternsauce.game.gamestate.ability.AbilityState.AbilityState
import com.easternsauce.game.gamestate.effect.EffectComponentType.EffectComponentType
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.gamestate.projectile.ProjectileComponentType.ProjectileComponentType
import com.easternsauce.game.math.Vector2f
import com.softwaremill.quicklens.ModifyPimp

trait Ability extends GameEntity {
  val params: AbilityParams

  def onActiveStart()(implicit game: CoreGame): Ability

  def update(delta: Float)(implicit game: CoreGame): Ability = {
    updateTimers(delta)
      .updateActivation()
  }

  def id: GameEntityId[Ability] = params.id
  def currentAreaId: AreaId = params.currentAreaId

  def currentState: AbilityState = params.state

  def currentStateTime: Float = params.stateTimer.time

  def channelTime: Float

  def finishWhenComponentsDestroyed: Boolean = false

  def range: Float = 0f

  def isMelee: Boolean = false

  private def updateTimers(delta: Float)(implicit game: CoreGame): Ability = {
    this
      .modify(_.params.stateTimer)
      .using(_.update(delta))
  }

  private def updateActivation()(implicit game: CoreGame): Ability = {
    this.transformIf(
      currentState == AbilityState.Channelling && currentStateTime > channelTime
    ) {
      this
        .modify(_.params.state)
        .setTo(AbilityState.Active)
        .modify(_.params.stateTimer)
        .using(_.restart())
        .onActiveStart()
    }
  }

  def spawnProjectile(
      componentType: ProjectileComponentType,
      pos: Vector2f,
      facing: Vector2f,
      damage: Float,
      expirationTime: Option[Float] = None
  )(implicit game: CoreGame): Unit = {

    game.queues.projectileComponentQueue.enqueue(
      ProjectileComponentToCreate(
        abilityId = id,
        componentType = componentType,
        currentAreaId = currentAreaId,
        creatureId = params.creatureId,
        pos = pos,
        facingVector = facing,
        damage = damage,
        scenarioStepNo = 0, // you can remove later
        expirationTime = expirationTime
      )
    )
  }

  def spawnEffect(
      componentType: EffectComponentType,
      pos: Vector2f,
      facing: Vector2f,
      damage: Float,
      expirationTime: Option[Float] = None
  )(implicit game: CoreGame): Unit = {

    game.queues.effectComponentQueue.enqueue(
      EffectComponentToCreate(
        abilityId = id,
        componentType = componentType,
        currentAreaId = currentAreaId,
        creatureId = params.creatureId,
        pos = pos,
        facingVector = facing,
        damage = damage,
        scenarioStepNo = 0,
        expirationTime = expirationTime
      )
    )
  }

  def copy(params: AbilityParams): Ability

}
