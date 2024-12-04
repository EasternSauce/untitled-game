package com.easternsauce.game.gamestate.creature

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.WorldDirection.WorldDirection
import com.easternsauce.game.gamestate.creature.CreatureType.CreatureType
import com.easternsauce.game.gamestate.creature.behavior.CreatureBehavior
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.gamestate.{GameEntity, WorldDirection}
import com.easternsauce.game.math.Vector2f
import com.easternsauce.game.util.TransformIf
import com.softwaremill.quicklens.{ModifyPimp, QuicklensMapAt}

case class Creature(params: CreatureParams, behavior: CreatureBehavior)
    extends GameEntity
    with TransformIf {
  def id: GameEntityId[Creature] = params.id

  def pos: Vector2f = params.pos

  def facingDirection: WorldDirection = {
    WorldDirection.fromVector(params.facingVector)
  }

  def isMoving: Boolean = params.velocity.length > 0

  def isAlive: Boolean = params.life > 0

  def isInvisible: Boolean = !params.isRespawnDelayInProgress

  def update(
      delta: Float,
      newPos: Option[Vector2f]
  )(implicit game: CoreGame): Creature = {
    this
      .updateTimers(delta)
      .updateAutonomousBehavior()
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
      .modify(_.params.recentlyHitTimer)
      .using(_.update(delta))
  }

  protected def updateAutonomousBehavior()(implicit
      game: CoreGame
  ): Creature = {
    behavior.run().apply(this)
  }

  private def updateMovement(
      newPos: Option[Vector2f]
  )(implicit game: CoreGame): Creature = {
    val vectorTowardsDest = pos.vectorTowards(params.destination)

    val movementStopCondition = vectorTowardsDest.length <= 0.4f

    val velocity =
      if (isAlive && !params.isDestinationReached && !movementStopCondition) {
        vectorTowardsDest.withLength(params.baseSpeed)
      } else {
        Vector2f(0f, 0f)
      }

    this
      .modify(_.params.pos)
      .setToIf(newPos.nonEmpty)(newPos.get)
      .modify(_.params.velocity)
      .setTo(velocity)
      .modify(_.params.isDestinationReached)
      .setToIf(movementStopCondition)(true)
  }

  def currentAreaId: AreaId = params.currentAreaId
}

object Creature {
  def producePlayer(
      creatureId: GameEntityId[Creature],
      currentAreaId: AreaId,
      pos: Vector2f,
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
        isPlayer = true,
        baseSpeed = creatureType.baseSpeed,
        life = creatureType.maxLife,
        maxLife = creatureType.maxLife,
        damage = creatureType.damage,
        animationDefinition = creatureType.animationDefinition,
        attackRange = creatureType.attackRange,
        primaryWeaponType = creatureType.primaryWeaponType,
        secondaryWeaponType = creatureType.secondaryWeaponType,
        isRenderBodyOnly = creatureType.isRenderBodyOnly,
        spawnPointId = spawnPointId,
        creatureType = creatureType
      ),
      behavior = NeutralBehavior()
    )
  }

  def produceEnemy(
      creatureId: GameEntityId[Creature],
      currentAreaId: AreaId,
      pos: Vector2f,
      creatureType: CreatureType,
      spawnPointId: Option[String]
  ): Creature = {
    new Creature(
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
        isPlayer = false,
        baseSpeed = creatureType.baseSpeed,
        life = creatureType.maxLife,
        maxLife = creatureType.maxLife,
        damage = creatureType.damage,
        animationDefinition = creatureType.animationDefinition,
        attackRange = creatureType.attackRange,
        primaryWeaponType = creatureType.primaryWeaponType,
        secondaryWeaponType = creatureType.secondaryWeaponType,
        isRenderBodyOnly = creatureType.isRenderBodyOnly,
        spawnPointId = spawnPointId,
        creatureType = creatureType
      ),
      behavior = EnemyBehavior()
    )
  }
}
