package com.easternsauce.game.gameview

import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId

case class CreatureRenderers() {
  private var creatureRenderers: Map[GameEntityId[Creature], CreatureRenderer] =
    _

  def init(gameState: GameState): Unit = {
    creatureRenderers = Map()
  }

//  def renderLifeBars(
//                      spriteBatches: SpriteBatches,
//                      gameState: GameState
//                    ): Unit = {
//    creatureRenderers.values.foreach(
//      _.renderLifeBar(spriteBatches.worldSpriteBatch, gameState)
//    )
//  }
//
//  def renderPlayerNames(
//                         spriteBatches: SpriteBatches,
//                         skin: Skin,
//                         gameState: GameState
//                       ): Unit = {
//    creatureRenderers.values.foreach(
//      _.renderPlayerName(
//        spriteBatches.worldTextSpriteBatch,
//        skin.getFont("default-font"),
//        gameState
//      )
//    )
//  }

  def getRenderersForAliveCreatures(
      gameState: GameState
  ): List[CreatureRenderer] = {
    gameState.creatures
      .filter { case (_, creature) =>
        creature.alive && creatureRenderers.contains(creature.id)
      }
      .keys
      .toList
      .map(creatureId => creatureRenderers(creatureId))
  }

  def getRenderersForDeadCreatures(
      gameState: GameState
  ): List[CreatureRenderer] = {
    gameState.creatures
      .filter { case (_, creature) =>
        !creature.alive && creatureRenderers.contains(creature.id)
      }
      .keys
      .toList
      .map(creatureId => creatureRenderers(creatureId))
  }

  def update(gameState: GameState): Unit = {
    val creatureRenderersToCreate =
      gameState.activeCreatureIds -- creatureRenderers.keys.toSet
    val creatureRenderersToDestroy =
      creatureRenderers.keys.toSet -- gameState.activeCreatureIds

    creatureRenderersToCreate.foreach(createCreatureRenderer(_, gameState))
    creatureRenderersToDestroy.foreach(destroyCreatureRenderer(_, gameState))
  }

  private def createCreatureRenderer(
      creatureId: GameEntityId[Creature],
      gameState: GameState
  ): Unit = {
    val creatureRenderer = CreatureRenderer(creatureId)
    creatureRenderer.init(gameState)
    creatureRenderers = creatureRenderers.updated(creatureId, creatureRenderer)
  }

  private def destroyCreatureRenderer(
      creatureId: GameEntityId[Creature],
      gameState: GameState
  ): Unit = {
    creatureRenderers = creatureRenderers.removed(creatureId)
  }

}
