package com.easternsauce.game.gamephysics

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable

case class CreatureUpdater() {

  private var creatureBodies: mutable.Map[GameEntityId[Creature], CreatureBody] = _
  private var areaWorlds: mutable.Map[AreaId, AreaWorld] = _

  def init(
      creatureBodies: mutable.Map[GameEntityId[Creature], CreatureBody],
      areaWorlds: mutable.Map[AreaId, AreaWorld]
  ): Unit = {
    this.creatureBodies = creatureBodies
    this.areaWorlds = areaWorlds
  }

  def update(areaId: AreaId)(implicit game: CoreGame): Unit = {
    creatureBodies
      .filter { case (id, _) =>
        game.gameState.creatures(id).params.currentAreaId == areaId
      }
      .values
      .foreach { body =>
        body.update(1f / 60f)
      }
  }

  def synchronize(areaId: AreaId)(implicit game: CoreGame): Unit = {
    val ents = game.gameState.creatures

    val toCreate =
      ents.values.filter(c => !creatureBodies.contains(c.id) && c.params.currentAreaId == areaId)

    val toDestroy =
      creatureBodies.values.filter(b => !ents.contains(b.creatureId) || b.areaId != areaId)

    toCreate.foreach { c =>
      val body = CreatureBody(c.id)
      body.init(areaWorlds(c.params.currentAreaId), c.pos)
      creatureBodies.update(c.id, body)
    }

    toDestroy.foreach { b =>
      b.onRemove()
      creatureBodies.remove(b.creatureId)
    }
  }

  def correctPositions(areaId: AreaId)(implicit game: CoreGame): Unit = {
    game.gameState.creatures.values.foreach { c =>
      if (
        c.params.currentAreaId == areaId &&
        creatureBodies.contains(c.id) &&
        creatureBodies(c.id).pos.distance(c.pos) > Constants.PhysicsBodyCorrectionDistance
      ) {
        creatureBodies(c.id).setPos(c.pos)
      }
    }
  }

  def teleportIfInArea(
      id: GameEntityId[Creature],
      pos: Vector2f,
      areaId: AreaId,
      game: CoreGame
  ): Unit =
    if (game.gameState.creatures.get(id).exists(_.params.currentAreaId == areaId)) {
      creatureBodies(id).setPos(pos)
    }
}
