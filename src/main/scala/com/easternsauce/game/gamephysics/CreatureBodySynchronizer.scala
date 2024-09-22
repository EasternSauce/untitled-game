package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}

import scala.collection.mutable

case class CreatureBodySynchronizer() {
  private var creatureBodies
      : mutable.Map[GameEntityId[Creature], CreatureBody] = _
  private var areaWorlds: mutable.Map[AreaId, AreaWorld] = _

  def init(
      creatureBodies: mutable.Map[GameEntityId[Creature], CreatureBody],
      areaWorlds: mutable.Map[AreaId, AreaWorld]
  ): Unit = {
    this.creatureBodies = creatureBodies
    this.areaWorlds = areaWorlds
  }

  def synchronizeForArea(areaId: AreaId)(implicit game: CoreGame): Unit = {
    val creatureBodiesToCreate =
      (game.gameState.activeCreatureIds -- creatureBodies.keys.toSet)
        .filter(game.gameState.creatures(_).currentAreaId == areaId)
    val creatureBodiesToDestroy =
      (creatureBodies.keys.toSet -- game.gameState.activeCreatureIds).filter(
        creatureId =>
          !game.gameState.creatures.contains(creatureId) ||
            game.gameState.creatures(creatureId).currentAreaId == areaId
      )

    creatureBodiesToCreate.foreach(createCreatureBody(_, game.gameState))
    creatureBodiesToDestroy.foreach(destroyCreatureBody(_, game.gameState))

    updateCreatureSensorsForArea(areaId, game)
  }

  private def updateCreatureSensorsForArea(
      areaId: AreaId,
      game: CoreGame
  ): Unit = {
    game.gameState.activeCreatureIds
      .filter(creatureId =>
        game.gameState.creatures(creatureId).params.currentAreaId == areaId
      )
      .foreach(creatureId => {
        val creature = game.gameState.creatures(creatureId)

        if (creature.alive) {
          if (creatureBodies(creature.id).isSensor) {
            creatureBodies(creature.id).setNonSensor()
          }
        } else {
          if (!creatureBodies(creature.id).isSensor) {
            creatureBodies(creature.id).setSensor()
          }
        }
      })
  }

  private def createCreatureBody(
      creatureId: GameEntityId[Creature],
      gameState: GameState
  )(implicit game: CoreGame): Unit = {
    val creature = gameState.creatures(creatureId)

    val creatureBody = CreatureBody(creatureId)

    val areaWorld = areaWorlds(creature.params.currentAreaId)

    creatureBody.init(areaWorld, creature.pos)

    creatureBodies.update(creatureId, creatureBody)
  }

  private def destroyCreatureBody(
      creatureId: GameEntityId[Creature],
      gameState: GameState
  ): Unit = {
    if (creatureBodies.contains(creatureId)) {
      creatureBodies(creatureId).onRemove()
      creatureBodies.remove(creatureId)
    }
  }

}
