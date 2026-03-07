package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gamestate.id.GameEntityId
import scala.collection.mutable

case class AbilitySpawner() {

  private var abilityBodies: mutable.Map[GameEntityId[AbilityComponent], AbilityBody] = _
  private var areaWorlds: mutable.Map[AreaId, AreaWorld] = _

  def init(
      areaWorlds: mutable.Map[AreaId, AreaWorld],
      existingAbilities: Iterable[AbilityComponent]
  )(implicit game: CoreGame): Unit = {
    abilityBodies = mutable.Map()
    this.areaWorlds = areaWorlds

    existingAbilities.foreach { ability =>
      val body = AbilityBody(ability.id)
      body.init(areaWorlds(ability.currentAreaId), ability.pos)
      abilityBodies(ability.id) = body
    }
  }

  def spawn(ability: AbilityComponent)(implicit game: CoreGame): Unit = {
    val body = AbilityBody(ability.id)
    body.init(areaWorlds(ability.currentAreaId), ability.pos)
    abilityBodies(ability.id) = body
  }

  def remove(abilityId: GameEntityId[AbilityComponent]): Unit = {
    abilityBodies.remove(abilityId)
  }

  def allBodies: mutable.Map[GameEntityId[AbilityComponent], AbilityBody] = abilityBodies
}
