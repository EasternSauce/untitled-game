package com.easternsauce.game.gameview

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable

case class CreatureRenderController() {

  private val creatureRenderables =
    mutable.Map[GameEntityId[Creature], CreatureRenderable]()

  def init(): Unit =
    creatureRenderables.clear()

  // -----------------------
  // Required by SceneView
  // -----------------------

  def getAliveCreatureRenderables(areaId: AreaId)(implicit
      game: CoreGame
  ): List[CreatureRenderable] =
    aliveCreatureRenderables(areaId)

  def synchronizeRenderables(areaId: AreaId)(implicit
      game: CoreGame
  ): Unit =
    synchronize(areaId)

  // -----------------------
  // Synchronization
  // -----------------------

  def synchronize(areaId: AreaId)(implicit game: CoreGame): Unit = {

    val currentIds =
      game.gameState.creatures.values
        .filter(_.currentAreaId == areaId)
        .map(_.id)
        .toSet

    currentIds.foreach { id =>
      if (!creatureRenderables.contains(id)) {
        val r = CreatureRenderable(id)
        r.init()
        creatureRenderables(id) = r
      }
    }

    creatureRenderables.keys
      .filterNot(currentIds.contains)
      .foreach(creatureRenderables.remove)
  }

  // -----------------------
  // Rendering
  // -----------------------

  def renderLifeBars(
      areaId: AreaId,
      worldBatch: RenderBatch
  )(implicit game: CoreGame): Unit =
    aliveCreatureRenderables(areaId)
      .foreach(_.renderLifeBar(worldBatch))

  def renderPlayerNames(
      areaId: AreaId,
      worldTextBatch: RenderBatch,
      skin: Skin
  )(implicit game: CoreGame): Unit = {

    val font = skin.getFont("default-font")

    aliveCreatureRenderables(areaId)
      .foreach(_.renderPlayerName(worldTextBatch, font))
  }

  def renderDeadCreatures(
      areaId: AreaId,
      worldBatch: RenderBatch,
      worldCameraPos: Vector2f
  )(implicit game: CoreGame): Unit =
    deadCreatureRenderables(areaId)
      .foreach(_.render(worldBatch, worldCameraPos))

  // -----------------------
  // Helpers
  // -----------------------

  private def aliveCreatureRenderables(areaId: AreaId)(implicit
      game: CoreGame
  ): List[CreatureRenderable] =
    creatureRenderables.collect {
      case (id, renderable)
          if game.gameState.creatures(id).isAlive &&
            game.gameState.creatures(id).currentAreaId == areaId =>
        renderable
    }.toList

  private def deadCreatureRenderables(areaId: AreaId)(implicit
      game: CoreGame
  ): List[CreatureRenderable] =
    creatureRenderables.collect {
      case (id, renderable)
          if !game.gameState.creatures(id).isAlive &&
            game.gameState.creatures(id).currentAreaId == areaId =>
        renderable
    }.toList
}
