package com.easternsauce.game.gamephysics

import com.easternsauce.game.Constants
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f

case class CreatureBodyPhysics() {
  private var creatureBodies: Map[GameEntityId[Creature], CreatureBody] = _
  private var areaWorlds: Map[AreaId, AreaWorld] = _

  def init(areaWorlds: Map[AreaId, AreaWorld]): Unit = {
    creatureBodies = Map()
    this.areaWorlds = areaWorlds
  }

  def update(areaId: AreaId, gameState: GameState): Unit = {
    creatureBodies
      .filter { case (creatureId, _) =>
        gameState.creatures(creatureId).params.currentAreaId == areaId
      }
      .values
      .foreach(_.update(gameState))
  }

  def synchronize(areaId: AreaId, gameState: GameState): Unit = {
    val creatureBodiesToCreate =
      gameState.activeCreatureIds -- creatureBodies.keys.toSet
    val creatureBodiesToDestroy =
      creatureBodies.keys.toSet -- gameState.activeCreatureIds

    creatureBodiesToCreate
      .filter(creatureId =>
        gameState.creatures(creatureId).params.currentAreaId == areaId
      )
      .foreach(createCreatureBody(_, gameState))
    creatureBodiesToDestroy
      .filter(creatureId =>
        !gameState.creatures.contains(creatureId) ||
          gameState.creatures(creatureId).params.currentAreaId == areaId
      )
      .foreach(destroyCreatureBody(_, gameState))

    gameState.activeCreatureIds
      .filter(creatureId =>
        gameState.creatures(creatureId).params.currentAreaId == areaId
      )
      .foreach(creatureId => {
        val creature = gameState.creatures(creatureId)

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

  def correctBodyPositions(areaId: AreaId, gameState: GameState): Unit = {
    gameState.creatures.values.foreach(creature =>
      if (
        creature.params.currentAreaId == areaId &&
        creatureBodies.contains(creature.id) && creatureBodies(creature.id).pos
          .distance(creature.pos) > Constants.PhysicsBodyCorrectionDistance
      ) {
        creatureBodies(creature.id).setPos(creature.pos)
      }
    )
  }

  def creatureBodyPositions: Map[GameEntityId[Creature], Vector2f] = {
    creatureBodies.values
      .map(creatureBody => {
        val pos = creatureBody.pos
        (creatureBody.creatureId, pos)
      })
      .toMap
  }

  private def createCreatureBody(
      creatureId: GameEntityId[Creature],
      gameState: GameState
  ): Unit = {
    val creature = gameState.creatures(creatureId)

    val creatureBody = CreatureBody(creatureId)

    val areaWorld = areaWorlds(creature.params.currentAreaId)

    creatureBody.init(areaWorld, creature.pos, gameState)

    creatureBodies = creatureBodies.updated(creatureId, creatureBody)
  }

  private def destroyCreatureBody(
      creatureId: GameEntityId[Creature],
      gameState: GameState
  ): Unit = {
    if (creatureBodies.contains(creatureId)) {
      creatureBodies(creatureId).onRemove()
      creatureBodies = creatureBodies.removed(creatureId)
    }
  }

  def setBodyPos(creatureId: GameEntityId[Creature], pos: Vector2f): Unit = {
    creatureBodies(creatureId).setPos(pos)
  }

  def setSensor(creatureId: GameEntityId[Creature]): Unit = {
    creatureBodies(creatureId).setSensor()
  }

  def setNonSensor(creatureId: GameEntityId[Creature]): Unit = {
    creatureBodies(creatureId).setNonSensor()
  }
}
