package com.easternsauce.game.gamestate.creature

import com.easternsauce.game.gamestate.WorldDirection.WorldDirection
import com.easternsauce.game.gamestate.id.GameEntityId
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
      gameState: GameState
  ): Creature = {
    this
      .updateTimers(delta)
      .updateMovement(gameState)
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

  def updateMovement(gameState: GameState): Creature = {
    this
  }
}
