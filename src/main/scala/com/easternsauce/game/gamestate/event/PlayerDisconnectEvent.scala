package com.easternsauce.game.gamestate.event

import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId
import com.softwaremill.quicklens.ModifyPimp

case class PlayerDisconnectEvent(creatureId: GameEntityId[Creature])
    extends GameStateEvent {
  override def applyToGameState(gameState: GameState): GameState = {
    gameState.modify(_.activeCreatureIds).using(_ - creatureId)
  }
}
