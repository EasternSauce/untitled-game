package com.easternsauce.game.gameview

import com.easternsauce.game.CoreGame
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId

//noinspection SpellCheckingInspection
case class CreatureRenderablesSynchronizer() {
  private var creatureRenderables
      : Map[GameEntityId[Creature], CreatureRenderable] = _

  def init(
      creatureRenderables: Map[GameEntityId[Creature], CreatureRenderable]
  ): Unit = {
    this.creatureRenderables = creatureRenderables
  }

  def synchronize()(implicit game: CoreGame): Unit = {
    val creatureRenderablesToCreate =
      game.gameState.activeCreatureIds -- creatureRenderables.keys.toSet
    val creatureRendererablesToDestroy =
      creatureRenderables.keys.toSet -- game.gameState.activeCreatureIds

    creatureRenderablesToCreate.foreach(createCreatureRenderable(_))
    creatureRendererablesToDestroy.foreach(
      destroyCreatureRenderable(_)
    )
  }

  private def createCreatureRenderable(
      creatureId: GameEntityId[Creature]
  )(implicit game: CoreGame): Unit = {
    val creatureRenderer = CreatureRenderable(creatureId)
    creatureRenderer.init()
    creatureRenderables =
      creatureRenderables.updated(creatureId, creatureRenderer)
  }

  private def destroyCreatureRenderable(
      creatureId: GameEntityId[Creature]
  )(implicit game: CoreGame): Unit = {
    creatureRenderables = creatureRenderables.removed(creatureId)
  }
}
