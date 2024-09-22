package com.easternsauce.game.gamestate.ability

import com.easternsauce.game.gamestate.WorldDirection.WorldDirection
import com.easternsauce.game.gamestate.creature.FramesDefinition
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.gamestate.{GameEntity, WorldDirection}
import com.easternsauce.game.math.Vector2f
import com.softwaremill.quicklens.ModifyPimp

trait Ability extends GameEntity {
  val params: AbilityParams

  val textureFileName: String

  val textureSize: Int
  val bodyRadius: Float

  val framesDefinition: FramesDefinition

  val speed: Float = 0f

  def id: GameEntityId[Ability] = params.id

  def pos: Vector2f = params.pos

  def facingDirection: WorldDirection = {
    WorldDirection.fromVector(params.facingVector)
  }

  def update(
      delta: Float,
      newPos: Option[Vector2f]
  ): Ability = {
    updateTimers(delta)
      .updateMovement(newPos)
      .updateFacingVector()
  }

  private def updateMovement(newPos: Option[Vector2f]): Ability = {
    if (newPos.nonEmpty) {
      this
        .modify(_.params.pos)
        .setTo(newPos.get)
    } else { this }
  }

  private def updateTimers(delta: Float): Ability = {
    this
      .modify(_.params.animationTimer)
      .using(_.update(delta))
  }

  private def updateFacingVector(): Ability = {
    this
      .modify(_.params.facingVector)
      .setToIf(velocity.length > 0)(velocity)
  }

  def velocity: Vector2f = {
    this.params.facingVector.normalized.multiply(this.speed)
  }

  def currentAreaId: AreaId = params.currentAreaId

  def destroyedOnContact: Boolean

  def copy(params: AbilityParams): Ability

}
