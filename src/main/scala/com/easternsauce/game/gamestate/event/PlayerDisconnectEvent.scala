package com.easternsauce.game.gamestate.event

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.creature.Creature
import com.easternsauce.game.gamestate.id.GameEntityId
import com.softwaremill.quicklens.ModifyPimp

case class PlayerDisconnectEvent(creatureId: GameEntityId[Creature])
    extends OperationalGameStateEvent {
  override def applyToGameState(
      gameState: GameState
  )(implicit game: CoreGame): GameState = {
    gameState.modify(_.activePlayerIds).using(_ - creatureId)
  }

}
