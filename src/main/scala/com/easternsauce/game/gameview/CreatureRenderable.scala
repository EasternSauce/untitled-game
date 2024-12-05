package com.easternsauce.game.gameview

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.CreatureAnimationType.CreatureAnimationType
import com.easternsauce.game.gamestate.creature.{
  Creature,
  CreatureAnimationType,
  PrimaryWeaponType,
  SecondaryWeaponType
}
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.{GameRectangle, IsometricProjection, Vector2f}

//noinspection SpellCheckingInspection
case class CreatureRenderable(creatureId: GameEntityId[Creature])
    extends Renderable {

  private var animations: Map[CreatureAnimationType, CreatureAnimation] = _

  def init()(implicit game: CoreGame): Unit = {
    val creature = game.gameState.creatures(creatureId)

    animations = {
      def entry(
          creatureAnimationType: CreatureAnimationType,
          creatureId: GameEntityId[Creature]
      ): (CreatureAnimationType, CreatureAnimation) = {
        creatureAnimationType -> CreatureAnimation(
          creatureId,
          creatureAnimationType
        )
      }

      creature.params.texturePaths.keys.map(entry(_, creatureId)).toMap
    }

    animations.values.foreach(_.init())
  }

  override def pos()(implicit game: CoreGame): Vector2f = {
    val creature = game.gameState.creatures(creatureId)

    creature.pos
  }

  override def areaId(gameState: GameState): AreaId = {
    val creature = gameState.creatures(creatureId)

    creature.params.currentAreaId
  }

  override def render(batch: GameSpriteBatch, worldCameraPos: Vector2f)(implicit
      game: CoreGame
  ): Unit = {
    val creature = game.gameState.creatures(creatureId)

    if (creature.isInvisible) {
      animations(CreatureAnimationType.Body).render(batch)
      if (!creature.params.isRenderBodyOnly) {
        animations(CreatureAnimationType.Head).render(batch)
      }
      if (creature.params.primaryWeaponType != PrimaryWeaponType.None) {
        animations(CreatureAnimationType.Weapon).render(batch)
      }
      if (creature.params.secondaryWeaponType == SecondaryWeaponType.Shield) {
        animations(CreatureAnimationType.Shield).render(batch)
      }
    }
  }

  def renderLifeBar(
      spriteBatch: GameSpriteBatch
  )(implicit game: CoreGame): Unit = {
    if (game.gameState.creatures.contains(creatureId)) {
      val creature = game.gameState.creatures(creatureId)

      if (creature.isAlive) {
        val lifeBarWidth = 32f
        val currentLifeBarWidth =
          lifeBarWidth * creature.params.life / creature.params.maxLife

        val creatureScreenPos =
          IsometricProjection.translatePosIsoToScreen(creature.pos)

        val barPos = Vector2f(
          creatureScreenPos.x - lifeBarWidth / 2f,
          creatureScreenPos.y + 48f
        )

        renderBar(spriteBatch, barPos, lifeBarWidth, Color.ORANGE)
        renderBar(spriteBatch, barPos, currentLifeBarWidth, Color.RED)
      }
    }
  }

  def renderPlayerName(spriteBatch: GameSpriteBatch, font: BitmapFont)(implicit
      game: CoreGame
  ): Unit = {
    if (
      game.gameState.creatures
        .contains(creatureId) && game.gameState
        .creatures(creatureId)
        .params
        .isPlayer
    ) {
      val creature = game.gameState.creatures(creatureId)

      if (creature.isAlive) {
        val creatureScreenPos =
          IsometricProjection.translatePosIsoToScreen(creature.pos)

        val namePos = Vector2f(
          creatureScreenPos.x - 25f,
          creatureScreenPos.y + 70
        )

        spriteBatch.drawFont(font, creature.id.value, namePos)
      }
    }
  }

  private def renderBar(
      spriteBatch: GameSpriteBatch,
      barPos: Vector2f,
      lifeBarWidth: Float,
      color: Color
  ): Unit = {
    val lifeBarHeight = 3f
    spriteBatch.filledRectangle(
      GameRectangle(barPos.x, barPos.y, lifeBarWidth, lifeBarHeight),
      color
    )
  }

  override def hasRenderPriority(gameState: GameState): Boolean = {
    val creature = gameState.creatures(creatureId)

    creature.isAlive
  }
}
