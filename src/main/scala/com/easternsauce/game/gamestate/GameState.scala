package com.easternsauce.game.gamestate

import com.easternsauce.game.CoreGame
import com.easternsauce.game.gamestate.creature.{Creature, CreatureFactory}
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f
import com.softwaremill.quicklens.{ModifyPimp, QuicklensMapAt}

case class GameState(
    cameraPos: Vector2f = Vector2f(10, 420),
    creatures: Map[GameEntityId[Creature], Creature] = Map(
      GameEntityId("player") -> CreatureFactory.male1(
        creatureId = GameEntityId("player"),
        currentAreaId = AreaId("area1"),
        pos = Vector2f(10, 420),
        player = true,
        baseSpeed = 1.0f
      )
    ),
    activeCreatureIds: Set[GameEntityId[Creature]] = Set(
      GameEntityId("player")
    ),
    currentAreaId: AreaId = AreaId("area1")
) {
  def update(
      delta: Float,
      game: CoreGame
  ): GameState = {
    this
      .modify(_.creatures.each)
      .using(creature =>
        if (activeCreatureIds.contains(creature.id)) {
          creature.update(delta, game.gameState)
        } else { creature }
      )
  }

}
