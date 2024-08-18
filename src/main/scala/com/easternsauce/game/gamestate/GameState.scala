package com.easternsauce.game.gamestate

import com.easternsauce.game.gamestate.creature.{Creature, CreatureFactory}
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.math.Vector2f
import com.easternsauce.game.{Constants, CoreGame}
import com.softwaremill.quicklens.{ModifyPimp, QuicklensMapAt}

case class GameState(
    creatures: Map[GameEntityId[Creature], Creature] = Map(),
    activeCreatureIds: Set[GameEntityId[Creature]] = Set()
) {
  def update(
      delta: Float,
      game: CoreGame
  ): GameState = {
    this
      .updateCreatures(delta, game)
      .handleCreatePlayers(game)
  }

  def updateCreatures(delta: Float, game: CoreGame): GameState = {
    this
      .modify(_.creatures.each)
      .using(creature =>
        if (activeCreatureIds.contains(creature.id)) {
          creature.update(
            delta,
            game.gamePhysics.creatureBodyPositions.get(creature.id),
            game.gameState
          )
        } else {
          creature
        }
      )
  }

  def handleCreatePlayers(game: CoreGame): GameState = {
    val result = game.playersToCreate.foldLeft(this) { case (gameState, name) =>
      println("trying to create" + name)
      val creatureId = GameEntityId[Creature](name)

      if (gameState.creatures.contains(creatureId)) {
        gameState
          .modify(_.activeCreatureIds)
          .using(_ + creatureId)
      } else {
        println("adding new creature")
        gameState
          .modify(_.creatures)
          .using(
            _.updated(
              creatureId,
              CreatureFactory
                .male1(
                  creatureId,
                  Constants.DefaultAreaId,
                  Vector2f(
                    5f + 3f * Math.random().toFloat,
                    415f + 3f * Math.random().toFloat
                  ),
                  player = true,
                  8f
                )
            )
          )
          .modify(_.activeCreatureIds)
          .using(_ + creatureId)
      }
    }
    game.playersToCreate = List()
    result
  }
}
