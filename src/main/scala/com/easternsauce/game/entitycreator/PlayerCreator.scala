package com.easternsauce.game.entitycreator

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.creature.CreatureType
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f
import com.softwaremill.quicklens.ModifyPimp

trait PlayerCreator {
  this: GameState =>

  private[entitycreator] def createPlayers(implicit game: CoreGame): GameState = {
    // drain all players from the player queue
    val playersToCreate = game.queues.playerQueue.drain()

    val result = playersToCreate.foldLeft(this) { (gameState, playerToCreate) =>
      createPlayer(gameState, playerToCreate)
    }

    result
  }

  private def createPlayer(
      gameState: GameState,
      playerToCreate: PlayerToCreate
  ): GameState = {
    val creatureId = GameEntityId[Creature](playerToCreate.clientId)

    if (gameState.creatures.contains(creatureId)) {
      gameState.modify(_.activePlayerIds).using(_ + creatureId)
    } else {
      gameState
        .modify(_.creatures)
        .using(
          _.updated(
            creatureId,
            Creature.producePlayer(
              creatureId,
              Constants.DefaultAreaId,
              Vector2f(55f, 415f),
              creatureType = CreatureType.Human,
              spawnPointId = None
            )
          )
        )
        .modify(_.activePlayerIds)
        .using(_ + creatureId)
    }
  }
}
