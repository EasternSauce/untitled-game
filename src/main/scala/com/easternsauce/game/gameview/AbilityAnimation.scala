package com.easternsauce.game.gameview

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{Animation, TextureRegion}
import com.easternsauce.game.Assets
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.Ability
import com.easternsauce.game.gamestate.creature.FramesDefinition
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.{IsometricProjection, Vector2f}

case class AbilityAnimation(abilityId: GameEntityId[Ability]) {
  private var animation: Animation[TextureRegion] = _
  private var texture: Texture = _

  def init()(implicit game: CoreGame): Unit = {
    val ability: Ability = game.gameState.abilities(abilityId)

    texture = Assets.texture("ability/" + ability.textureFileName)

    animation = loadAnimation(
      ability.textureSize,
      ability.textureSize,
      ability.framesDefinition
    )

  }

  private def loadAnimation(
      frameWidth: Int,
      frameHeight: Int,
      framesDefinition: FramesDefinition
  ): Animation[TextureRegion] = {

    val frames = for {
      j <-
        (framesDefinition.start until framesDefinition.start + framesDefinition.count).toArray
    } yield new TextureRegion(
      texture,
      j * frameWidth,
      0,
      frameWidth,
      frameHeight
    )

    new Animation[TextureRegion](
      framesDefinition.frameDuration,
      frames: _*
    )

  }

  def render(batch: GameSpriteBatch)(implicit game: CoreGame): Unit = {
    val ability = game.gameState.abilities(abilityId)

    val frame = animation.getKeyFrame(ability.params.animationTimer.time, false)

    val pos = IsometricProjection.translatePosIsoToScreen(
      Vector2f(ability.pos.x, ability.pos.y)
    )

    val angle = IsometricProjection
      .translatePosIsoToScreen(ability.params.facingVector)
      .angleDeg

    batch.draw(
      frame,
      pos.x - ability.textureSize / 2f,
      pos.y - ability.textureSize / 2f,
      ability.textureSize,
      ability.textureSize,
      angle
    )

  }
}
