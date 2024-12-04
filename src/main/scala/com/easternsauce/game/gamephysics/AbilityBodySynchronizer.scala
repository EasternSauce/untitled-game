package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}

import scala.collection.mutable

case class AbilityBodySynchronizer() {
  private var abilityBodies
      : mutable.Map[GameEntityId[AbilityComponent], AbilityComponentBody] = _
  private var areaWorlds: mutable.Map[AreaId, AreaWorld] = _

  def init(
      abilityBodies: mutable.Map[GameEntityId[
        AbilityComponent
      ], AbilityComponentBody],
      areaWorlds: mutable.Map[AreaId, AreaWorld]
  ): Unit = {
    this.abilityBodies = abilityBodies
    this.areaWorlds = areaWorlds
  }

  def synchronize(areaId: AreaId)(implicit game: CoreGame): Unit = {
    val abilityBodiesToCreate =
      (game.gameState.abilityComponents.keys.toSet -- abilityBodies.keys.toSet)
        .filter(game.gameState.abilityComponents(_).currentAreaId == areaId)
    val abilityBodiesToDestroy =
      (abilityBodies.keys.toSet -- game.gameState.abilityComponents.keys.toSet)
        .filter(abilityId =>
          !game.gameState.abilityComponents.contains(abilityId) ||
            game.gameState.abilityComponents(abilityId).currentAreaId == areaId
        )

    abilityBodiesToCreate.foreach(createAbilityBody(_, game.gameState))
    abilityBodiesToDestroy.foreach(destroyAbilityBody(_, game.gameState))
  }

  private def createAbilityBody(
      abilityId: GameEntityId[AbilityComponent],
      gameState: GameState
  )(implicit game: CoreGame): Unit = {
    val ability = gameState.abilityComponents(abilityId)

    val abilityBody = AbilityComponentBody(abilityId)

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
