package com.easternsauce.game.gamestate.creature

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.WorldDirection.WorldDirection
import com.easternsauce.game.gamestate.creature.CreatureType.CreatureType
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.gamestate.{GameEntity, WorldDirection}
import com.easternsauce.game.math.Vector2f
import com.softwaremill.quicklens.{ModifyPimp, QuicklensMapAt}

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
      newPos: Option[Vector2f]
  )(implicit game: CoreGame): Creature = {
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
      .modify(_.params.abilityCooldownTimers.each)
      .using(_.update(delta))
  }

  private def updateMovement(
      newPos: Option[Vector2f]
  )(implicit game: CoreGame): Creature = {
    val vectorTowardsDest = pos.vectorTowards(params.destination)

    val movementStopCondition = vectorTowardsDest.length <= 0.4f

    val velocity =
      if (alive && !params.destinationReached && !movementStopCondition) {
        vectorTowardsDest.withLength(params.baseSpeed)
      } else {
        Vector2f(0f, 0f)
      }

    this
      .modify(_.params.pos)
      .setToIf(newPos.nonEmpty)(newPos.get)
      .modify(_.params.velocity)
      .setTo(velocity)
      .modify(_.params.destinationReached)
      .setToIf(movementStopCondition)(true)
  }

  def currentAreaId: AreaId = params.currentAreaId
}

object Creature {
  def produce(
      creatureId: GameEntityId[Creature],
      currentAreaId: AreaId,
      pos: Vector2f,
      player: Boolean,
      creatureType: CreatureType,
      spawnPointId: Option[String]
  ): Creature = {
    Creature(
      CreatureParams(
        id = creatureId,
        currentAreaId = currentAreaId,
        pos = pos,
        destination = pos,
        lastPos = pos,
        texturePaths = creatureType.texturePaths,
        textureSize = creatureType.textureSize,
        spriteVerticalShift = creatureType.spriteVerticalShift,
        bodyRadius = creatureType.bodyRadius,
        player = player,
        baseSpeed = creatureType.baseSpeed,
        life = creatureType.maxLife,
        maxLife = creatureType.maxLife,
        damage = creatureType.damage,
        animationDefinition = creatureType.animationDefinition,
        attackRange = creatureType.attackRange,
        primaryWeaponType = creatureType.primaryWeaponType,
        secondaryWeaponType = creatureType.secondaryWeaponType,
        renderBodyOnly = creatureType.renderBodyOnly,
        spawnPointId = spawnPointId,
        creatureType = creatureType
      )
    )
  }
}
