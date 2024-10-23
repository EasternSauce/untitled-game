package com.easternsauce.game.entitycreator

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState

trait EntityCreator {
  private[entitycreator] def createEntities(implicit
      game: CoreGame
  ): GameState => GameState
}
