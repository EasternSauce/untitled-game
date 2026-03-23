package com.easternsauce.game.gamestate.effect

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameEntity
import com.easternsauce.game.gamestate.WorldDirection
import com.easternsauce.game.gamestate.WorldDirection.WorldDirection
import com.easternsauce.game.gamestate.ability.Ability
import com.easternsauce.game.gamestate.ability.EffectComponentParams
import com.easternsauce.game.gamestate.creature.FramesDefinition
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f
import com.easternsauce.game.util.TransformIf
import com.softwaremill.quicklens.ModifyPimp

trait EffectComponent extends GameEntity with TransformIf {
  val params: EffectComponentParams

  val textureFileName: String

  val textureSize: Int
  val bodyRadius: Float

  val framesDefinition: FramesDefinition

  val speed: Float = 0f

  def id: GameEntityId[EffectComponent] = params.id

  def pos: Vector2f = params.pos

  def facingDirection: WorldDirection = {
    WorldDirection.fromVector(params.facingVector)
  }

  def init(): EffectComponent

  def update(
      delta: Float,
      newPos: Option[Vector2f]
  )(implicit game: CoreGame): EffectComponent = {
    updateTimers(delta)
      .updateMovement(newPos)
      .updateFacingVector()
      .removeIfExpired()
  }

  private def updateMovement(newPos: Option[Vector2f]): EffectComponent = {
    this.transformIf(newPos.nonEmpty) {
      this
        .modify(_.params.pos)
        .setTo(newPos.get)
    }
  }

  private def updateTimers(delta: Float): EffectComponent = {
    this
      .modify(_.params.generalTimer)
      .using(_.update(delta))
  }

  private def updateFacingVector(): EffectComponent = {
    this
      .modify(_.params.facingVector)
      .setToIf(velocity.len > 0)(velocity)
  }

  private def removeIfExpired(): EffectComponent = {
    this.transformIf(
      params.expirationTime.nonEmpty && params.generalTimer.time > params.expirationTime.get
    ) {
      this.modify(_.params.isScheduledToBeRemoved).setTo(true)
    }
  }

  def velocity: Vector2f = {
    this.params.facingVector.normalized.multiply(this.speed)
  }

  def abilityId: GameEntityId[Ability] = params.abilityId
  def currentAreaId: AreaId = params.currentAreaId

  def isDestroyedOnCreatureContact: Boolean

  def isDestroyedOnTerrainContact: Boolean

  def withAbility(f: Ability => Unit)(implicit game: CoreGame): Unit =
    game.gameState.abilities.get(params.abilityId).foreach(f)

  def copy(params: EffectComponentParams): EffectComponent

}
