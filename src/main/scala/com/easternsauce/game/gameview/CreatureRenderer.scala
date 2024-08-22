package com.easternsauce.game.gameview

import com.easternsauce.game.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

//noinspection SpellCheckingInspection
case class CreatureRenderer() {
  private var creatureRenderables
      : Map[GameEntityId[Creature], CreatureRenderable] = _
  private var creatureRenderablesSynchronizer: CreatureRenderablesSynchronizer =
    _

  def init(): Unit = {
    creatureRenderables = Map()

    creatureRenderablesSynchronizer = CreatureRenderablesSynchronizer()
    creatureRenderablesSynchronizer.init(creatureRenderables)
  }

//  def renderLifeBars(
//                      spriteBatches: SpriteBatches,
//                      gameState: GameState
//                    ): Unit = {
//    creatureRenderables.values.foreach(
//      _.renderLifeBar(spriteBatches.worldSpriteBatch, gameState)
//    )
//  }
//
//  def renderPlayerNames(
//                         spriteBatches: SpriteBatches,
//                         skin: Skin,
//                         gameState: GameState
//                       ): Unit = {
//    creatureRenderables.values.foreach(
//      _.renderPlayerName(
//        spriteBatches.worldTextSpriteBatch,
//        skin.getFont("default-font"),
//        gameState
//      )
//    )
//  }

  def renderAliveCreatures(
      worldSpriteBatch: GameSpriteBatch,
      worldCameraPos: Vector2f,
      gameState: GameState
  ): Unit = {
    renderablesForAliveCreatures(gameState)
      .foreach(_.render(worldSpriteBatch, worldCameraPos, gameState))
  }

  def renderDeadCreatures(
      worldSpriteBatch: GameSpriteBatch,
      worldCameraPos: Vector2f,
      gameState: GameState
  ): Unit = {
    renderablesForDeadCreatures(gameState)
      .foreach(_.render(worldSpriteBatch, worldCameraPos, gameState))
  }

  private def renderablesForAliveCreatures(
      gameState: GameState
  ): List[CreatureRenderable] = {
    gameState.creatures
      .filter { case (_, creature) =>
        creature.alive && creatureRenderables.contains(creature.id)
      }
      .keys
      .toList
      .map(creatureId => creatureRenderables(creatureId))
  }

  private def renderablesForDeadCreatures(
      gameState: GameState
  ): List[CreatureRenderable] = {
    gameState.creatures
      .filter { case (_, creature) =>
        !creature.alive && creatureRenderables.contains(creature.id)
      }
      .keys
      .toList
      .map(creatureId => creatureRenderables(creatureId))
  }

  def synchronizeRenderables()(implicit game: CoreGame): Unit = {
    creatureRenderablesSynchronizer.synchronize()
  }

}
