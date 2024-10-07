package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}

import scala.collection.mutable

case class AbilityBodySynchronizer() {
  private var abilityBodies
      : mutable.Map[GameEntityId[AbilityComponent], AbilityBody] = _
  private var areaWorlds: mutable.Map[AreaId, AreaWorld] = _

  def init(
      abilityBodies: mutable.Map[GameEntityId[AbilityComponent], AbilityBody],
      areaWorlds: mutable.Map[AreaId, AreaWorld]
  ): Unit = {
    this.abilityBodies = abilityBodies
    this.areaWorlds = areaWorlds
  }

  def synchronizeForArea(areaId: AreaId)(implicit game: CoreGame): Unit = {
    val abilityBodiesToCreate =
      (game.gameState.abilities.keys.toSet -- abilityBodies.keys.toSet)
        .filter(game.gameState.abilities(_).currentAreaId == areaId)
    val abilityBodiesToDestroy =
      (abilityBodies.keys.toSet -- game.gameState.abilities.keys.toSet).filter(
        abilityId =>
          !game.gameState.abilities.contains(abilityId) ||
            game.gameState.abilities(abilityId).currentAreaId == areaId
      )

    abilityBodiesToCreate.foreach(createAbilityBody(_, game.gameState))
    abilityBodiesToDestroy.foreach(destroyAbilityBody(_, game.gameState))
  }

  private def createAbilityBody(
      abilityId: GameEntityId[AbilityComponent],
      gameState: GameState
  )(implicit game: CoreGame): Unit = {
    val ability = gameState.abilities(abilityId)

    val abilityBody = AbilityBody(abilityId)

    val areaWorld = areaWorlds(ability.params.currentAreaId)

    abilityBody.init(areaWorld, ability.pos)

    abilityBodies.update(abilityId, abilityBody)
  }

  private def destroyAbilityBody(
      abilityId: GameEntityId[AbilityComponent],
      gameState: GameState
  ): Unit = {
    if (abilityBodies.contains(abilityId)) {
      abilityBodies(abilityId).onRemove()
      abilityBodies.remove(abilityId)
    }
  }

}
