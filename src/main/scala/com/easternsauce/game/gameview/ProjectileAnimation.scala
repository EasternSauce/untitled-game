package com.easternsauce.game.gameview

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.easternsauce.game.Assets
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.FramesDefinition
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.gamestate.projectile.ProjectileComponent
import com.easternsauce.game.math.IsometricProjection
import com.easternsauce.game.math.Vector2f

case class ProjectileAnimation(projectileComponentId: GameEntityId[ProjectileComponent]) {
  private var animation: Animation[TextureRegion] = _
  private var texture: Texture = _

  def init()(implicit game: CoreGame): Unit = {
    val projectile: ProjectileComponent = game.gameState.projectileComponents(projectileComponentId)

    texture = Assets.texture("ability/" + projectile.textureFileName)

    animation = loadAnimation(
      projectile.textureSize,
      projectile.textureSize,
      projectile.framesDefinition
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

  def render(batch: RenderBatch)(implicit game: CoreGame): Unit = {
    val component = game.gameState.projectileComponents(projectileComponentId)

    val frame = animation.getKeyFrame(component.params.generalTimer.time, false)

    val pos = IsometricProjection.isoToScreenCompensated(
      Vector2f(component.pos.x, component.pos.y)
    )

    val angle = IsometricProjection
      .isoToScreenCompensated(component.params.facingVector)
      .angleDeg

    batch.draw(
      frame,
      pos.x - component.textureSize / 2f,
      pos.y - component.textureSize / 2f,
      component.textureSize,
      component.textureSize,
      angle
    )

  }
}
