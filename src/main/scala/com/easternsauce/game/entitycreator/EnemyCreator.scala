package com.easternsauce.game.entitycreator

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId
import com.softwaremill.quicklens.ModifyPimp

trait EnemyCreator {
  this: GameState =>

  private[entitycreator] def createEnemies(implicit game: CoreGame): GameState = {
    // drain items from the enemy queue
    val enemiesToCreate = game.queues.enemyQueue.drain()

    val result = enemiesToCreate.foldLeft(this) { (gameState, enemyToCreate) =>
      createEnemy(gameState, enemyToCreate)
    }

    result
  }

  private def createEnemy(
      gameState: GameState,
      enemyToCreate: EnemyToCreate
  ): GameState = {
    val enemyId = GameEntityId[Creature](
      "enemy" + (Math.random() * 1000000).toInt
    )

    gameState
      .modify(_.creatures)
      .using(
        _.updated(
          enemyId,
          Creature.produceEnemy(
            enemyId,
            enemyToCreate.areaId,
            enemyToCreate.pos,
            creatureType = enemyToCreate.creatureType,
            spawnPointId = Some(enemyToCreate.spawnPointId)
          )
        )
      )
  }
}
