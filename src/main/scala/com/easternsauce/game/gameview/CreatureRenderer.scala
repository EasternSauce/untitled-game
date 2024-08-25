package com.easternsauce.game.gameview

import com.easternsauce.game.CoreGame
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable

//noinspection SpellCheckingInspection
case class CreatureRenderer() {
  private var creatureRenderables
      : mutable.Map[GameEntityId[Creature], CreatureRenderable] = _
  private var creatureRenderablesSynchronizer: CreatureRenderablesSynchronizer =
    _

  def init(): Unit = {
    creatureRenderables = mutable.Map()

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

  def renderAliveCreaturesForArea(
      areaId: AreaId,
      worldSpriteBatch: GameSpriteBatch,
      worldCameraPos: Vector2f
  )(implicit game: CoreGame): Unit = {
    renderablesForAliveCreaturesInArea(areaId)
      .foreach(_.render(worldSpriteBatch, worldCameraPos))
  }

  def renderDeadCreatures(
      areaId: AreaId,
      worldSpriteBatch: GameSpriteBatch,
      worldCameraPos: Vector2f
  )(implicit game: CoreGame): Unit = {
    renderablesForDeadCreaturesInArea(areaId)
      .foreach(_.render(worldSpriteBatch, worldCameraPos))
  }

  private def renderablesForAliveCreaturesInArea(areaId: AreaId)(implicit
      game: CoreGame
  ): List[CreatureRenderable] = {
    game.gameState.creatures
      .filter { case (_, creature) =>
        creature.alive && creature.currentAreaId == areaId && creatureRenderables
          .contains(creature.id)
      }
      .keys
      .toList
      .map(creatureId => creatureRenderables(creatureId))
  }

  private def renderablesForDeadCreaturesInArea(areaId: AreaId)(implicit
      game: CoreGame
  ): List[CreatureRenderable] = {
    game.gameState.creatures
      .filter { case (_, creature) =>
        !creature.alive && creature.currentAreaId == areaId && creatureRenderables
          .contains(creature.id)
      }
      .keys
      .toList
      .map(creatureId => creatureRenderables(creatureId))
  }

  def synchronizeRenderables(areaId: AreaId)(implicit game: CoreGame): Unit = {
    creatureRenderablesSynchronizer.synchronizeForArea(areaId)
  }

}
