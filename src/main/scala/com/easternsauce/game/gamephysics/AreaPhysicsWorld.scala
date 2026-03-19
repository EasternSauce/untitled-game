package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.ability.AbilityComponent
import com.easternsauce.game.gamestate.event.AbilityComponentHitsCreatureEvent
import com.easternsauce.game.gamestate.event.AbilityComponentHitsTerrainEvent
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f
import com.softwaremill.quicklens.ModifyPimp

import scala.collection.mutable

case class AreaPhysicsWorld(areaId: AreaId) {

  private val chunkSize = 2f

  private val staticChunks =
    mutable.Map[(Int, Int), mutable.Set[PhysicsBody]]()

  private val dynamicChunks =
    mutable.Map[(Int, Int), mutable.Set[PhysicsBody]]()

  private val staticBodies =
    mutable.Set[PhysicsBody]()

  private val dynamicBodies =
    mutable.Set[PhysicsBody]()

  def registerBody(body: PhysicsBody): Unit = {
    if (body.isStatic) staticBodies.add(body)
    else dynamicBodies.add(body)
  }

  def removeBody(body: PhysicsBody): Unit = {
    staticBodies.remove(body)
    dynamicBodies.remove(body)
  }

  def allBodies: Iterable[PhysicsBody] =
    staticBodies ++ dynamicBodies

  def buildStaticChunks(): Unit = {
    staticChunks.clear()

    staticBodies.foreach { body =>
      val chunk = chunkOf(body.pos)
      val set = staticChunks.getOrElseUpdate(chunk, mutable.Set())
      set.add(body)
    }
  }

  def update()(implicit game: CoreGame): Unit = {

    // rebuild dynamic chunks AFTER movement
    dynamicChunks.clear()

    dynamicBodies.foreach { body =>
      val chunk = chunkOf(body.pos)
      val set = dynamicChunks.getOrElseUpdate(chunk, mutable.Set())
      set.add(body)
    }
  }

  // =========================
  // PER-BODY COLLISION (NEW)
  // =========================

  def resolveCollisionsForBody(body: PhysicsBody)(implicit game: CoreGame): Unit = {
    val (cx, cy) = chunkOf(body.pos)

    // circle vs rect (static)
    for {
      x <- cx - 1 to cx + 1
      y <- cy - 1 to cy + 1
      neighbor <- staticChunks.getOrElse((x, y), Set.empty)
    } {
      resolveCircleVsRect(body, neighbor)
    }

    // circle vs circle (dynamic)
    for {
      x <- cx - 1 to cx + 1
      y <- cy - 1 to cy + 1
      neighbor <- dynamicChunks.getOrElse((x, y), Set.empty)
      if neighbor != body
    } {
      resolveCircleVsCircle(body, neighbor)
    }
  }

  private def chunkOf(pos: Vector2f): (Int, Int) = {
    val cx = Math.floor(pos.x / chunkSize).toInt
    val cy = Math.floor(pos.y / chunkSize).toInt
    (cx, cy)
  }

  private def resolveCircleVsCircle(a: PhysicsBody, b: PhysicsBody)(implicit
      game: CoreGame
  ): Unit = {
    val dx = b.pos.x - a.pos.x
    val dy = b.pos.y - a.pos.y
    val distSq = dx * dx + dy * dy

    val aRadius = a.radius
    val bRadius = b.radius

    val minDist = aRadius + bRadius
    if (distSq >= minDist * minDist) return

    val dist = Math.sqrt(distSq).toFloat
    val (nx, ny) = if (dist == 0f) (1f, 0f) else (dx / dist, dy / dist)
    val overlap = minDist - dist

    if (!a.isPushable && !b.isPushable) {} else if (!a.isPushable)
      b.setPos(Vector2f(b.pos.x + nx * overlap, b.pos.y + ny * overlap))
    else if (!b.isPushable) a.setPos(Vector2f(a.pos.x - nx * overlap, a.pos.y - ny * overlap))
    else {
      val half = overlap * 0.5f
      a.setPos(Vector2f(a.pos.x - nx * half, a.pos.y - ny * half))
      b.setPos(Vector2f(b.pos.x + nx * half, b.pos.y + ny * half))
    }

    // trigger ability hits
    val abilityComponents = game.gameState.abilityComponents.values
    val creatures = game.gameState.creatures.values.filter(_.isAlive)
    for {
      ac <- abilityComponents
      c <- creatures
      dist = (ac.pos - c.pos).len
      combined = ac.bodyRadius + c.params.bodyRadius
      if dist < combined
    } {
      game.queues.localEventQueue.enqueue(
        AbilityComponentHitsCreatureEvent(c.id, ac.id, ac.currentAreaId)
      )
      if (ac.isDestroyedOnCreatureContact) {
        val updatedAC = ac.modify(_.params.isScheduledToBeRemoved).setTo(true)
        game.gameState.abilityComponents.updated(ac.id, updatedAC)
      }
    }
  }

  private def resolveCircleVsRect(circle: PhysicsBody, rect: PhysicsBody)(implicit
      game: CoreGame
  ): Unit = {
    val cx = circle.pos.x
    val cy = circle.pos.y

    val rx = rect.pos.x
    val ry = rect.pos.y
    val half = rect.radius

    val closestX = clamp(cx, rx - half, rx + half)
    val closestY = clamp(cy, ry - half, ry + half)

    val dx = cx - closestX
    val dy = cy - closestY
    val distSq = dx * dx + dy * dy

    val circleRadius = circle.radius

    if (distSq >= circleRadius * circleRadius) return

    val dist = Math.sqrt(distSq).toFloat
    if (dist == 0f) {
      val left = cx - (rx - half)
      val right = (rx + half) - cx
      val down = cy - (ry - half)
      val up = (ry + half) - cy
      val min = Math.min(Math.min(left, right), Math.min(down, up))

      if (min == left) circle.setPos(Vector2f(rx - half - circleRadius, cy))
      else if (min == right) circle.setPos(Vector2f(rx + half + circleRadius, cy))
      else if (min == down) circle.setPos(Vector2f(cx, ry - half - circleRadius))
      else circle.setPos(Vector2f(cx, ry + half + circleRadius))
      return
    }

    val nx = dx / dist
    val ny = dy / dist
    val overlap = circleRadius - dist
    circle.setPos(Vector2f(cx + nx * overlap, cy + ny * overlap))

    circle match {
      case ac: AbilityComponent if ac.isDestroyedOnTerrainContact =>
        game.queues.localEventQueue.enqueue(
          AbilityComponentHitsTerrainEvent(ac.id, ac.currentAreaId)
        )
      case _ =>
    }
  }

  private def clamp(v: Float, min: Float, max: Float): Float =
    Math.max(min, Math.min(v, max))
}
