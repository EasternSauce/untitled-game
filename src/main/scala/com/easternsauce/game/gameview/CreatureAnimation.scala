package com.easternsauce.game.gameview

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{Animation, TextureRegion}
import com.easternsauce.game.Assets
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.WorldDirection
import com.easternsauce.game.gamestate.creature.CreatureAnimationType.CreatureAnimationType
import com.easternsauce.game.gamestate.creature.EquipmentSlotType.EquipmentSlotType
import com.easternsauce.game.gamestate.creature.{Creature, EquipmentSlotType, FramesDefinition, PrimaryWeaponType}
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.{IsometricProjection, Vector2f}

case class CreatureAnimation(
    creatureId: GameEntityId[Creature],
    creatureAnimationType: CreatureAnimationType
) {
  private var standstillAnimations: Array[Animation[TextureRegion]] = _
  private var attackAnimations: Array[Animation[TextureRegion]] = _
  private var walkAnimations: Array[Animation[TextureRegion]] = _
  private var deathAnimations: Array[Animation[TextureRegion]] = _
  private var spellcastAnimations: Array[Animation[TextureRegion]] = _
  private var bowAnimations: Array[Animation[TextureRegion]] = _
  private var texture: Texture = _

  def init()(implicit game: CoreGame): Unit = {
    val slotType: EquipmentSlotType = EquipmentSlotType.Weapon

    val creature: Creature = game.gameState.creatures(creatureId)

    texture =
      Assets.texture(creature.params.texturePaths(creatureAnimationType))

    val frameWidth = creature.params.animationDefinition.frameWidth
    val frameHeight = creature.params.animationDefinition.frameHeight

    standstillAnimations = loadAnimations(
      frameWidth,
      frameHeight,
      creature.params.animationDefinition.stanceFrames
    )
    attackAnimations = loadAnimations(
      frameWidth,
      frameHeight,
      creature.params.animationDefinition.attackFrames
    )
    walkAnimations = loadAnimations(
      frameWidth,
      frameHeight,
      creature.params.animationDefinition.walkFrames
    )
    deathAnimations = loadAnimations(
      frameWidth,
      frameHeight,
      creature.params.animationDefinition.deathFrames
    )
    if (creature.params.animationDefinition.spellcastFrames.nonEmpty) {
      spellcastAnimations = loadAnimations(
        frameWidth,
        frameHeight,
        creature.params.animationDefinition.spellcastFrames.get
      )
    }
    if (creature.params.animationDefinition.bowFrames.nonEmpty) {
      bowAnimations = loadAnimations(
        frameWidth,
        frameHeight,
        creature.params.animationDefinition.bowFrames.get
      )
    }

    // TODO: make this configurable
//    standstillAnimations.foreach(
//      _.setPlayMode(Animation.PlayMode.LOOP_PINGPONG)
//    )
  }

  private def loadAnimations(
      frameWidth: Int,
      frameHeight: Int,
      framesDefinition: FramesDefinition
  ): Array[Animation[TextureRegion]] = {
    for {
      i <- (0 until WorldDirection.values.size).toArray
    } yield {
      val frames =
        for {
          j <-
            (framesDefinition.start until framesDefinition.start + framesDefinition.count).toArray
        } yield new TextureRegion(
          texture,
          j * frameWidth,
          i * frameHeight,
          frameWidth,
          frameHeight
        )

      new Animation[TextureRegion](
        framesDefinition.frameDuration,
        frames: _*
      )
    }
  }

  def render(batch: GameSpriteBatch)(implicit game: CoreGame): Unit = {
    val creature = game.gameState.creatures(creatureId)

    val frame =
      if (
        creature.params.attackAnimationTimer.running && creature.params.attackAnimationTimer.time < creature.params.animationDefinition.attackFrames.totalDuration
      ) {
        if (creature.params.primaryWeaponType == PrimaryWeaponType.Bow) {
          bowAnimations(creature.facingDirection.id)
            .getKeyFrame(creature.params.attackAnimationTimer.time, false)
        } else {
          attackAnimations(creature.facingDirection.id)
            .getKeyFrame(creature.params.attackAnimationTimer.time, false)
        }
      } else if (creature.moving) {
        walkAnimations(creature.facingDirection.id)
          .getKeyFrame(creature.params.animationTimer.time, true)
      } else if (creature.alive) {
        standstillAnimations(creature.facingDirection.id)
          .getKeyFrame(creature.params.animationTimer.time, true)
      } else {
        deathAnimations(creature.facingDirection.id)
          .getKeyFrame(creature.params.deathAnimationTimer.time, false)
      }

    val pos = IsometricProjection.translatePosIsoToScreen(
      Vector2f(creature.pos.x, creature.pos.y)
    )

    batch.draw(
      frame,
      pos.x - creature.params.textureSize / 2f,
      pos.y - creature.params.textureSize / 2f + creature.params.spriteVerticalShift,
      creature.params.textureSize,
      creature.params.textureSize
    )
  }
}
