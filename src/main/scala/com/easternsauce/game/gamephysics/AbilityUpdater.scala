package com.easternsauce.game.gamephysics

import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.id.{AreaId, GameEntityId}
import com.easternsauce.game.math.Vector2f
import scala.collection.mutable

case class AbilityUpdater() {

  private var abilityBodies: mutable.Map[GameEntityId[AbilityComponent], AbilityBody] = _
  private var areaWorlds: mutable.Map[AreaId, AreaWorld] = _

  def init(
      abilityBodies: mutable.Map[GameEntityId[AbilityComponent], AbilityBody],
      areaWorlds: mutable.Map[AreaId, AreaWorld]
  ): Unit = {
    this.abilityBodies = abilityBodies
    this.areaWorlds = areaWorlds
  }

  def update(areaId: AreaId)(implicit game: CoreGame): Unit = {
    abilityBodies
      .filter { case (id, _) =>
        game.gameState.abilityComponents(id).params.currentAreaId == areaId
      }
      .values
      .foreach(_.update(game.gameState))
  }

  def synchronize(areaId: AreaId)(implicit game: CoreGame): Unit = {
    val ents = game.gameState.abilityComponents

    val toCreate =
      ents.values.filter(e => !abilityBodies.contains(e.id) && e.params.currentAreaId == areaId)

    val toDestroy =
      abilityBodies.values.filter(b => !ents.contains(b.abilityComponentId) || b.areaId != areaId)

    toCreate.foreach { e =>
      val body = AbilityBody(e.id)
      body.init(areaWorlds(e.params.currentAreaId), e.pos)
      abilityBodies.update(e.id, body)
    }

    toDestroy.foreach { b =>
      b.onRemove()
      abilityBodies.remove(b.abilityComponentId)
    }
  }

  def correctPositions(areaId: AreaId)(implicit game: CoreGame): Unit = {
    game.gameState.abilityComponents.values.foreach { ability =>
      if (
        ability.params.currentAreaId == areaId &&
        abilityBodies.contains(ability.id) &&
        abilityBodies(ability.id).pos.distance(
          ability.pos
        ) > Constants.PhysicsBodyCorrectionDistance
      ) {
        abilityBodies(ability.id).setPos(ability.pos)
      }
    }
  }

  def setBodyPosIfInArea(
      id: GameEntityId[AbilityComponent],
      pos: Vector2f,
      areaId: AreaId,
      game: CoreGame
  ): Unit =
    if (game.gameState.abilityComponents.get(id).exists(_.params.currentAreaId == areaId)) {
      abilityBodies(id).setPos(pos)
    }

  def setSensorIfInArea(id: GameEntityId[AbilityComponent], areaId: AreaId, game: CoreGame): Unit =
    if (game.gameState.abilityComponents.get(id).exists(_.params.currentAreaId == areaId)) {
      abilityBodies(id).setSensor()
    }

  def setNonSensorIfInArea(
      id: GameEntityId[AbilityComponent],
      areaId: AreaId,
      game: CoreGame
  ): Unit =
    if (game.gameState.abilityComponents.get(id).exists(_.params.currentAreaId == areaId)) {
      abilityBodies(id).setNonSensor()
    }
}
