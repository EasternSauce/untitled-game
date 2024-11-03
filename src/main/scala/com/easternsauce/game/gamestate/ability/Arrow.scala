package com.easternsauce.game.gamestate.ability
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.entitycreator.AbilityComponentToCreate

case class Arrow(params: AbilityParams) extends Ability {

  override def init()(implicit game: CoreGame): Ability = {
    game.queues.abilityComponentsToCreate += AbilityComponentToCreate(
      abilityId = params.id,
      componentType = AbilityComponentType.ArrowComponent,
      currentAreaId = currentAreaId,
      creatureId = params.creatureId,
      pos = params.pos,
      facingVector = params.facingVector,
      damage = params.damage
    )

    this
  }

  override def update()(implicit game: CoreGame): Ability = {
    this
  }

  override def copy(params: AbilityParams): Ability = Arrow(params)

}
