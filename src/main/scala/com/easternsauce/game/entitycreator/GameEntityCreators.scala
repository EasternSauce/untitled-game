package com.easternsauce.game.entitycreator

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState

case class GameEntityCreators() {
  private var playerCreator: PlayerCreator = _
  private var abilityCreator: AbilityCreator = _
  private var abilityComponentCreator: AbilityComponentCreator = _

  def init(): Unit = {
    playerCreator = PlayerCreator()
    abilityCreator = AbilityCreator()
    abilityComponentCreator = AbilityComponentCreator()
  }

  def createScheduledEntities()(implicit
      game: CoreGame
  ): GameState => GameState = {
    playerCreator.createEntities
      .andThen(abilityCreator.createEntities)
      .andThen(abilityComponentCreator.createEntities)
  }

}
