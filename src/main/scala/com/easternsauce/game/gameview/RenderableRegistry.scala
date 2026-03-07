package com.easternsauce.game.gameview

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameEntity
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId

import scala.collection.mutable

/** Base registry for keeping renderables in sync with game entities */
abstract class RenderableRegistry[
    E <: GameEntity,
    Id <: GameEntityId[E],
    R <: Renderable
] {

  protected var renderables: mutable.Map[Id, R] = _

  def init(renderables: mutable.Map[Id, R]): Unit =
    this.renderables = renderables

  /** Provides all current game entities */
  protected def entities(implicit game: CoreGame): Map[Id, E]

  /** Creates a new renderable for a given entity ID */
  protected def createRenderable(id: Id)(implicit game: CoreGame): R

  /** Destroys a renderable; can be overridden for cleanup */
  protected def destroyRenderable(id: Id)(implicit game: CoreGame): Unit =
    renderables.remove(id)

  /** Synchronizes renderables with game entities for the given area */
  def update(areaId: AreaId)(implicit game: CoreGame): Unit = {
    val ents = entities

    // Create renderables for entities in the current area that aren't already created
    val toCreate = ents.keysIterator.filter { id =>
      !renderables.contains(id) && ents(id).currentAreaId == areaId
    }

    // Destroy renderables that no longer exist or are in a different area
    val toDestroy = renderables.keysIterator.filter { id =>
      !ents.contains(id) || renderables(id).areaId(game.gameState) != areaId
    }

    toCreate.foreach(id => renderables.update(id, createRenderable(id)))
    toDestroy.foreach(destroyRenderable)
  }
}
