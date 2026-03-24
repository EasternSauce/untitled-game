package com.easternsauce.game.gameview

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable

case class CreatureRenderController() {

  private val renderables =
    mutable.Map[GameEntityId[Creature], CreatureRenderable]()

  def init(): Unit =
    renderables.clear()

  // -----------------------
  // SceneView API
  // -----------------------

  def getAliveCreatureRenderables(areaId: AreaId)(implicit
      game: CoreGame
  ): List[CreatureRenderable] =
    getRenderables(areaId, _.isAlive)

  def synchronizeRenderables(areaId: AreaId)(implicit game: CoreGame): Unit =
    synchronize(areaId)

  // -----------------------
  // Synchronization
  // -----------------------

  private def synchronize(areaId: AreaId)(implicit game: CoreGame): Unit = {
    val creaturesInArea = creaturesByArea(areaId)
    val currentIds = creaturesInArea.keySet

    // Add missing renderables
    currentIds.foreach { id =>
      renderables.getOrElseUpdate(
        id, {
          val r = CreatureRenderable(id)
          r.init()
          r
        }
      )
    }

    // Remove stale renderables
    renderables.keys
      .filterNot(currentIds.contains)
      .foreach(renderables.remove)
  }

  // -----------------------
  // Rendering
  // -----------------------

  def renderLifeBars(areaId: AreaId, batch: RenderBatch)(implicit game: CoreGame): Unit =
    getRenderables(areaId, _.isAlive)
      .foreach(_.renderLifeBar(batch))

  def renderPlayerNames(
      areaId: AreaId,
      textBatch: RenderBatch,
      skin: Skin
  )(implicit game: CoreGame): Unit = {

    val font = skin.getFont("default-font")

    getRenderables(areaId, _.isAlive)
      .foreach(_.renderPlayerName(textBatch, font))
  }

  def renderDeadCreatures(
      areaId: AreaId,
      batch: RenderBatch,
      cameraPos: Vector2f
  )(implicit game: CoreGame): Unit =
    getRenderables(areaId, c => !c.isAlive)
      .foreach(_.render(batch, cameraPos))

  // -----------------------
  // Helpers
  // -----------------------

  private def creaturesByArea(areaId: AreaId)(implicit
      game: CoreGame
  ): Map[GameEntityId[Creature], Creature] =
    game.gameState.creatures.filter { case (_, c) => c.currentAreaId == areaId }.toMap

  private def getRenderables(
      areaId: AreaId,
      predicate: Creature => Boolean
  )(implicit game: CoreGame): List[CreatureRenderable] = {

    val creatures = creaturesByArea(areaId)

    renderables.collect {
      case (id, renderable) if creatures.get(id).exists(predicate) =>
        renderable
    }.toList
  }
}
