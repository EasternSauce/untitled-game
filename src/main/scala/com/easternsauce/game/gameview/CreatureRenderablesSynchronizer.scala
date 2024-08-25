package com.easternsauce.game.gameview

import com.easternsauce.game.CoreGame
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}

import scala.collection.mutable

//noinspection SpellCheckingInspection
case class CreatureRenderablesSynchronizer() {
  private var creatureRenderables
      : mutable.Map[GameEntityId[Creature], CreatureRenderable] = _

  def init(
      creatureRenderables: mutable.Map[GameEntityId[
        Creature
      ], CreatureRenderable]
  ): Unit = {
    this.creatureRenderables = creatureRenderables
  }

  def synchronizeForArea(areaId: AreaId)(implicit game: CoreGame): Unit = {
    val creatureRenderablesToCreate =
      (game.gameState.activeCreatureIds -- creatureRenderables.keys.toSet)
        .filter(game.gameState.creatures(_).currentAreaId == areaId)
    val creatureRendererablesToDestroy =
      (creatureRenderables.keys.toSet -- game.gameState.activeCreatureIds)
        .filter(creatureId =>
          !game.gameState.creatures.contains(creatureId) ||
            game.gameState.creatures(creatureId).currentAreaId == areaId
        )

    creatureRenderablesToCreate.foreach(createCreatureRenderable(_))
    creatureRendererablesToDestroy.foreach(destroyCreatureRenderable(_))
  }

  private def createCreatureRenderable(
      creatureId: GameEntityId[Creature]
  )(implicit game: CoreGame): Unit = {
    val creatureRenderer = CreatureRenderable(creatureId)
    creatureRenderer.init()
    creatureRenderables.update(creatureId, creatureRenderer)
  }

  private def destroyCreatureRenderable(
      creatureId: GameEntityId[Creature]
  )(implicit game: CoreGame): Unit = {
    creatureRenderables.remove(creatureId)
  }
}
