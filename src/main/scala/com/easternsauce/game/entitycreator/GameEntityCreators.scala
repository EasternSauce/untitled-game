package com.easternsauce.game.entitycreator

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState

trait GameEntityCreators
    extends PlayerCreator
    with EnemyCreator
    with AbilityCreator
    with ProjectileComponentCreator
    with EffectComponentCreator {
  this: GameState =>
  def createEntities()(implicit
      game: CoreGame
  ): GameState = {
    createPlayers.createEnemies.createAbilities.createProjectileComponents.createEffectComponents
  }
}
