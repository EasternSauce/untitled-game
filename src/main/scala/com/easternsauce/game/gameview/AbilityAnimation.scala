package com.easternsauce.game.gameview

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{Animation, TextureRegion}
import com.easternsauce.game.Assets
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.WorldDirection
import com.easternsauce.game.gamestate.ability.Ability
import com.easternsauce.game.gamestate.creature.FramesDefinition
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.{IsometricProjection, Vector2f}

case class AbilityAnimation(abilityId: GameEntityId[Ability]) {
  private var animations: Array[Animation[TextureRegion]] = _
  private var texture: Texture = _

  def init()(implicit game: CoreGame): Unit = {
    val ability: Ability = game.gameState.abilities(abilityId)

    animations = new Array[Animation[TextureRegion]](WorldDirection.values.size)
    texture = Assets.texture(ability.textureFileName)

    animations = loadAnimations(
      ability.textureWidth,
      ability.textureHeight,
      ability.framesDefinition
    )

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
    val ability = game.gameState.abilities(abilityId)

    val frame =
      animations(ability.facingDirection.id)
        .getKeyFrame(ability.params.animationTimer.time, false)

    val pos = IsometricProjection.translatePosIsoToScreen(
      Vector2f(ability.pos.x, ability.pos.y)
    )

    batch.draw(
      frame,
      pos.x - ability.worldWidth / 2f,
      pos.y - ability.worldHeight / 2f,
      ability.worldWidth,
      ability.worldHeight
    )

  }
}
