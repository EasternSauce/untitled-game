package com.easternsauce.game.gameview

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import scala.collection.mutable

//noinspection SpellCheckingInspection
case class CreatureRenderableRegistry()
    extends RenderableRegistry[
      Creature,
      GameEntityId[Creature],
      CreatureRenderable
    ] {

  protected def entities(implicit game: CoreGame): Map[GameEntityId[Creature], Creature] = {
    val existing =
      game.gameState.activePlayerIds ++
        game.gameState.creatures.values
          .filterNot(_.params.isPlayer)
          .map(_.id)

    existing.map(id => id -> game.gameState.creatures(id)).toMap
  }

  protected def entityArea(entity: Creature): AreaId =
    entity.currentAreaId

  protected def createRenderable(
      id: GameEntityId[Creature]
  )(implicit game: CoreGame): CreatureRenderable = {
    val r = CreatureRenderable(id)
    r.init()
    r
  }
}
