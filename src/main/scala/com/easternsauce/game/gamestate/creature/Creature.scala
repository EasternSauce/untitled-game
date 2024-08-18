package com.easternsauce.game.gamestate.creature

import com.easternsauce.game.gamestate.WorldDirection.WorldDirection
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.gamestate.{GameEntity, GameState, WorldDirection}
import com.easternsauce.game.math.Vector2f
import com.softwaremill.quicklens.ModifyPimp

case class Creature(params: CreatureParams) extends GameEntity {

  def id: GameEntityId[Creature] = params.id

  def pos: Vector2f = params.pos

  def facingDirection: WorldDirection = {
    WorldDirection.fromVector(params.facingVector)
  }

  def moving: Boolean = params.velocity.length > 0

  def alive: Boolean = params.life > 0

  def invisible: Boolean = !params.respawnDelayInProgress

  def update(
      delta: Float,
      newPos: Option[Vector2f],
      gameState: GameState
  ): Creature = {
    this
      .updateTimers(delta)
      .updateMovement(newPos)
  }

  private def updateTimers(delta: Float): Creature = {
    this
      .modify(_.params.animationTimer)
      .using(_.update(delta))
      .modify(_.params.attackAnimationTimer)
      .using(_.update(delta))
      .modify(_.params.deathAnimationTimer)
      .using(_.update(delta))
  }

  def updateMovement(newPos: Option[Vector2f]): Creature = {
    val vectorTowardsDest = pos.vectorTowards(params.destination)

    val velocity = if (!alive) {
      Vector2f(0, 0)
    } else if (vectorTowardsDest.length > 0.4f) {
      vectorTowardsDest.withLength(params.baseSpeed)
    } else {
      vectorTowardsDest.withLength(0f)
    }

    this
      .modify(_.params.pos)
      .setToIf(newPos.nonEmpty)(newPos.get)
      .modify(_.params.velocity)
      .setTo(velocity)
  }

  def currentAreaId: AreaId = params.currentAreaId
}
