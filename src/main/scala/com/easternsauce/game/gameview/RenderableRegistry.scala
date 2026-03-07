package com.easternsauce.game.gameview

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameEntity
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}

import scala.collection.mutable

abstract class RenderableRegistry[
  E <: GameEntity,
  Id <: GameEntityId[E],
  R <: Renderable
] {

  protected var renderables: mutable.Map[Id, R] = _

  def init(renderables: mutable.Map[Id, R]): Unit =
    this.renderables = renderables

  protected def entities(implicit game: CoreGame): Map[Id, E]

  protected def entityArea(entity: E): AreaId

  protected def createRenderable(id: Id)(implicit game: CoreGame): R

  def update(areaId: AreaId)(implicit game: CoreGame): Unit = {
    val ents = entities

    val toCreate =
      (ents.keySet -- renderables.keySet)
        .filter(id => entityArea(ents(id)) == areaId)

    val toDestroy =
      (renderables.keySet -- ents.keySet)
        .filter(id =>
          !ents.contains(id) ||
            entityArea(ents(id)) == areaId
        )

    toCreate.foreach(id => renderables.update(id, createRenderable(id)))
    toDestroy.foreach(renderables.remove)
  }
}