package com.easternsauce.game.gamestate

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Buttons
import com.easternsauce.game.gamestate.creature.{Creature, CreatureFactory}
import com.easternsauce.game.gamestate.event.GameStateEvent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.{MousePosTransformations, Vector2f}
import com.easternsauce.game.{Constants, CoreGame}
import com.softwaremill.quicklens.{ModifyPimp, QuicklensMapAt}

case class GameState(
    creatures: Map[GameEntityId[Creature], Creature] = Map(),
    activeCreatureIds: Set[GameEntityId[Creature]] = Set()
) {
  def updateForArea(areaId: AreaId, delta: Float)(implicit
      game: CoreGame
  ): GameState = {
    val updatedGameState = this
      .updateCreaturesForArea(areaId, delta)
      .handleCreatePlayers() // TODO: server only?
      .handleClientInput()

    game.applyBroadcastedEvents(updatedGameState)
  }

  def applyEvents(events: List[GameStateEvent]) = {
    events.foldLeft(this){case (gameState, event) => event.applyToGameState(gameState)}
  }

  private def updateCreaturesForArea(
      areaId: AreaId,
      delta: Float
  )(implicit game: CoreGame): GameState = {
    this
      .modify(_.creatures.each)
      .using(creature =>
        if (
          activeCreatureIds
            .contains(creature.id) && creature.currentAreaId == areaId
        ) {
          creature.update(
            delta,
            game.physics.creatureBodyPositions.get(creature.id)
          )
        } else {
          creature
        }
      )
  }

  private def handleCreatePlayers()(implicit game: CoreGame): GameState = {
    val playersToCreate = game.playersToCreate

    game.clearPlayersToCreate()

    playersToCreate.foldLeft(this) { case (gameState, name) =>
      val creatureId = GameEntityId[Creature](name)

      if (gameState.creatures.contains(creatureId)) {
        gameState
          .modify(_.activeCreatureIds)
          .using(_ + creatureId)
      } else {
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
  }

  private def handleClientInput()(implicit game: CoreGame): GameState = {
    val clientCreature = game.clientCreatureId
      .filter(this.creatures.contains)
      .map(this.creatures(_))

    if (clientCreature.nonEmpty && game.keyHeld(Buttons.LEFT)) {
      val creature = clientCreature.get
      val vectorTowardsDestination =
        creature.pos.vectorTowards(creature.params.destination)

      val destination = MousePosTransformations.mouseWorldPos(
        Gdx.input.getX.toFloat,
        Gdx.input.getY.toFloat,
        creature.pos
      )

      this
        .modify(_.creatures.at(creature.id))
        .using(
          _.modify(_.params.destination)
            .setTo(destination)
        )
        .modify(_.creatures.at(creature.id).params.facingVector)
        .setToIf(vectorTowardsDestination.length > 0)(
          vectorTowardsDestination
        )
    } else {
      this
    }
  }
}
