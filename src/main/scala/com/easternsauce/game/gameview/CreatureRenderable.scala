package com.easternsauce.game.gameview

import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.CreatureAnimationType.CreatureAnimationType
import com.easternsauce.game.gamestate.creature.{Creature, CreatureAnimationType, PrimaryWeaponType, SecondaryWeaponType}
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

case class CreatureRenderable(creatureId: GameEntityId[Creature])
    extends Renderable {

  private var animations: Map[CreatureAnimationType, CreatureAnimation] = _

  def init(gameState: GameState): Unit = {
    val creature = gameState.creatures(creatureId)

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

    animations.values.foreach(_.init(gameState))
  }

  override def pos(gameState: GameState): Vector2f = {
    val creature = gameState.creatures(creatureId)

    creature.pos
  }

  override def areaId(gameState: GameState): AreaId = {
    val creature = gameState.creatures(creatureId)

    creature.params.currentAreaId
  }

  override def render(
      batch: GameSpriteBatch,
      worldCameraPos: Vector2f,
      gameState: GameState
  ): Unit = {
    val creature = gameState.creatures(creatureId)

    if (creature.invisible) {
      animations(CreatureAnimationType.Body).render(batch, gameState)
      if (!creature.params.renderBodyOnly) {
        animations(CreatureAnimationType.Head).render(batch, gameState)
      }
      if (creature.params.primaryWeaponType != PrimaryWeaponType.None) {
        animations(CreatureAnimationType.Weapon).render(batch, gameState)
      }
      if (creature.params.secondaryWeaponType == SecondaryWeaponType.Shield) {
        animations(CreatureAnimationType.Shield).render(batch, gameState)
      }
    }
  }

//  def renderLifeBar(spriteBatch: GameSpriteBatch, gameState: GameState): Unit = {
//    if (gameState.creatures.contains(creatureId)) {
//      val creature = gameState.creatures(creatureId)
//
//      if (!creature.params.deathAcknowledged) {
//        val lifeBarWidth = 32f
//        val currentLifeBarWidth =
//          lifeBarWidth * creature.params.life / creature.params.maxLife
//
//        val creatureScreenPos =
//          IsometricProjection.translatePosIsoToScreen(creature.pos)
//
//        val barPos = Vector2(
//          creatureScreenPos.x - lifeBarWidth / 2f,
//          creatureScreenPos.y + 48f
//        )
//
//        renderBar(spriteBatch, barPos, lifeBarWidth, Color.ORANGE)
//        renderBar(spriteBatch, barPos, currentLifeBarWidth, Color.RED)
//      }
//    }
//  }
//
//  def renderPlayerName(
//      spriteBatch: SpriteBatch,
//      font: BitmapFont,
//      gameState: GameState
//  ): Unit = {
//    if (
//      gameState.creatures
//        .contains(creatureId) && gameState.creatures(creatureId).params.player
//    ) {
//      val creature = gameState.creatures(creatureId)
//
//      if (!creature.params.deathAcknowledged) {
//        val creatureScreenPos =
//          IsometricProjection.translatePosIsoToScreen(creature.pos)
//
//        val namePos = Vector2(
//          creatureScreenPos.x - 25f,
//          creatureScreenPos.y + 70
//        )
//
//        spriteBatch.drawFont(font, creature.id.value, namePos)
//      }
//    }
//  }
//
//  private def renderBar(
//      spriteBatch: SpriteBatch,
//      barPos: Vector2,
//      lifeBarWidth: Float,
//      color: Color
//  ): Unit = {
//    val lifeBarHeight = 3f
//    spriteBatch.filledRectangle(
//      Rectangle(barPos.x, barPos.y, lifeBarWidth, lifeBarHeight),
//      color
//    )
//  }

  override def renderPriority(gameState: GameState): Boolean = {
    val creature = gameState.creatures(creatureId)

    creature.alive
  }
}
