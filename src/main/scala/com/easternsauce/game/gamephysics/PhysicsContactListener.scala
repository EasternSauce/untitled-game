package com.easternsauce.game.gamephysics

import com.badlogic.gdx.physics.box2d.{
  Contact,
  ContactImpulse,
  ContactListener,
  Manifold
}
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.event.{
  AbilityComponentHitsCreatureEvent,
  AbilityComponentHitsTerrainEvent
}

case class PhysicsContactListener()(implicit game: CoreGame)
    extends ContactListener {
  override def beginContact(contact: Contact): Unit = {
    val objA = contact.getFixtureA.getBody.getUserData
    val objB = contact.getFixtureB.getBody.getUserData

    onContactStart(objA, objB)
    onContactStart(objB, objA)
  }

  private def onContactStart(objA: Any, objB: Any): Unit = {
    (objA, objB) match {
      case (componentBody: AbilityComponentBody, creatureBody: CreatureBody) =>
        val component =
          game.gameState.abilityComponents.get(componentBody.abilityComponentId)

        component.foreach(component =>
          game.queues.localEvents += AbilityComponentHitsCreatureEvent(
            creatureBody.creatureId,
            componentBody.abilityComponentId,
            component.currentAreaId
          )
        )

      case (componentBody: AbilityComponentBody, _: MapTerrainBody) =>
        val component =
          game.gameState.abilityComponents.get(componentBody.abilityComponentId)

        component.foreach(component =>
          game.queues.localEvents += AbilityComponentHitsTerrainEvent(
            componentBody.abilityComponentId,
            component.currentAreaId
          )
        )

      case _ =>
    }
  }

  override def endContact(contact: Contact): Unit = {}

  override def preSolve(contact: Contact, oldManifold: Manifold): Unit = {}

  override def postSolve(contact: Contact, impulse: ContactImpulse): Unit = {}

  def onContactEnd(objA: Any, objB: Any): Unit = {
    (objA, objB) match {
      // ability body...
      case _ =>
    }
  }
}
