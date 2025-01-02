package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.gamestate.WorldDirection.WorldDirection
import com.easternsauce.game.gamestate.creature.FramesDefinition
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.gamestate.{GameEntity, WorldDirection}
import com.easternsauce.game.math.Vector2f
import com.easternsauce.game.util.TransformIf
import com.softwaremill.quicklens.ModifyPimp

trait AbilityComponent extends GameEntity with TransformIf {
  val params: AbilityComponentParams

  val textureFileName: String

  val textureSize: Int
  val bodyRadius: Float

  val framesDefinition: FramesDefinition

  val speed: Float = 0f

  def id: GameEntityId[AbilityComponent] = params.id

  def pos: Vector2f = params.pos

  def facingDirection: WorldDirection = {
    WorldDirection.fromVector(params.facingVector)
  }

  def init(): AbilityComponent

  def update(
      delta: Float,
      newPos: Option[Vector2f]
  ): AbilityComponent = {
    updateTimers(delta)
      .updateMovement(newPos)
      .updateFacingVector()
      .removeIfExpired()
  }

  private def updateMovement(newPos: Option[Vector2f]): AbilityComponent = {
    this.transformIf(newPos.nonEmpty) {
      this
        .modify(_.params.pos)
        .setTo(newPos.get)
    }
  }

  private def updateTimers(delta: Float): AbilityComponent = {
    this
      .modify(_.params.generalTimer)
      .using(_.update(delta))
  }

  private def updateFacingVector(): AbilityComponent = {
    this
      .modify(_.params.facingVector)
      .setToIf(velocity.len > 0)(velocity)
  }

  private def removeIfExpired(): AbilityComponent = {
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

  def copy(params: AbilityComponentParams): AbilityComponent

}
