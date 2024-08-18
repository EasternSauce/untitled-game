package com.easternsauce.game.gamephysics

import com.badlogic.gdx.physics.box2d.{Contact, ContactImpulse, ContactListener, Manifold}

case class PhysicsContactListener(physics: GamePhysics)
    extends ContactListener {
  override def beginContact(contact: Contact): Unit = {
    val objA = contact.getFixtureA.getBody.getUserData
    val objB = contact.getFixtureB.getBody.getUserData

    onContactStart(objA, objB)
    onContactStart(objB, objA)
  }

  def onContactStart(objA: Any, objB: Any): Unit = {
    (objA, objB) match {
      //ability body....
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
