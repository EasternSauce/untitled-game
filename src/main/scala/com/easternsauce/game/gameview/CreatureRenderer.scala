package com.easternsauce.game.gameview

import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

case class CreatureRenderer() {
  private var creatureRenderables
      : Map[GameEntityId[Creature], CreatureRenderable] = _

  def init(): Unit = {
    creatureRenderables = Map()
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

  def updateRenderables(gameState: GameState): Unit = {
    val creatureRenderablesToCreate =
      gameState.activeCreatureIds -- creatureRenderables.keys.toSet
    val creatureRendererablesToDestroy =
      creatureRenderables.keys.toSet -- gameState.activeCreatureIds

    creatureRenderablesToCreate.foreach(createCreatureRenderable(_, gameState))
    creatureRendererablesToDestroy.foreach(
      destroyCreatureRenderable(_, gameState)
    )
  }

  private def createCreatureRenderable(
      creatureId: GameEntityId[Creature],
      gameState: GameState
  ): Unit = {
    val creatureRenderer = CreatureRenderable(creatureId)
    creatureRenderer.init(gameState)
    creatureRenderables =
      creatureRenderables.updated(creatureId, creatureRenderer)
  }

  private def destroyCreatureRenderable(
      creatureId: GameEntityId[Creature],
      gameState: GameState
  ): Unit = {
    creatureRenderables = creatureRenderables.removed(creatureId)
  }

}
