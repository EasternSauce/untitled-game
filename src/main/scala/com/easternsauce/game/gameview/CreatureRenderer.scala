package com.easternsauce.game.gameview

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable

//noinspection SpellCheckingInspection
case class CreatureRenderer() {
  private var creatureRenderables
      : mutable.Map[GameEntityId[Creature], CreatureRenderable] = _
  private var creatureRenderableSynchronizer: CreatureRenderableSynchronizer =
    _

  def init(): Unit = {
    creatureRenderables = mutable.Map()

    creatureRenderableSynchronizer = CreatureRenderableSynchronizer()
    creatureRenderableSynchronizer.init(creatureRenderables)
  }

  def renderLifeBarsForArea(
      areaId: AreaId,
      worldSpriteBatch: GameSpriteBatch
  )(implicit game: CoreGame): Unit = {
    creatureRenderables.values.foreach(
      _.renderLifeBar(worldSpriteBatch)
    )
  }

  def renderPlayerNamesForArea(
      areaId: AreaId,
      worldTextSpriteBatch: GameSpriteBatch,
      skin: Skin
  )(implicit game: CoreGame): Unit = {
    creatureRenderables.values.foreach(
      _.renderPlayerName(worldTextSpriteBatch, skin.getFont("default-font"))
    )
  }

  def renderAliveCreaturesForArea(
      areaId: AreaId,
      worldSpriteBatch: GameSpriteBatch,
      worldCameraPos: Vector2f
  )(implicit game: CoreGame): Unit = {
    renderablesForAliveCreaturesInArea(areaId)
      .foreach(_.renderCreature(worldSpriteBatch, worldCameraPos))
  }

  def renderDeadCreatures(
      areaId: AreaId,
      worldSpriteBatch: GameSpriteBatch,
      worldCameraPos: Vector2f
  )(implicit game: CoreGame): Unit = {
    renderablesForDeadCreaturesInArea(areaId)
      .foreach(_.renderCreature(worldSpriteBatch, worldCameraPos))
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
    creatureRenderableSynchronizer.synchronizeForArea(areaId)
  }

}
