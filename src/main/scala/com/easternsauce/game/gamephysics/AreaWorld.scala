package com.easternsauce.game.gamephysics

import com.easternsauce.game.gamestate.id.AreaId

import scala.collection.mutable

case class AreaWorld(areaId: AreaId) {

  private val bodies: mutable.Set[PhysicsBody] = mutable.Set()

  def registerBody(body: PhysicsBody): Unit =
    bodies.add(body)

  def removeBody(body: PhysicsBody): Unit =
    bodies.remove(body)

  def allBodies: Iterable[PhysicsBody] =
    bodies

  def update(): Unit = {
    // collisions will run here later
  }

}
