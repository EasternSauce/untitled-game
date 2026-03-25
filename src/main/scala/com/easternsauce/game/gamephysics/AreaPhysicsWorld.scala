package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.event.ProjectileComponentHitsCreatureEvent
import com.easternsauce.game.gamestate.event.ProjectileComponentHitsTerrainEvent
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

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

    for {
      x <- cx - 1 to cx + 1
      y <- cy - 1 to cy + 1
      neighbor <- staticChunks.getOrElse((x, y), Set.empty)
    } {
      resolveCollision(body, neighbor)
    }

    for {
      x <- cx - 1 to cx + 1
      y <- cy - 1 to cy + 1
      neighbor <- dynamicChunks.getOrElse((x, y), Set.empty)
      if neighbor != body
    } {
      resolveCollision(body, neighbor)
    }
  }

  private def resolveCollision(a: PhysicsBody, b: PhysicsBody)(implicit game: CoreGame): Unit = {
    (a.shape, b.shape) match {
      case (CircleShape(_), CircleShape(_)) =>
        resolveCircleVsCircle(a, b)

      case (CircleShape(_), RectShape(_, _)) =>
        resolveCircleVsRect(a, b)

      case (RectShape(_, _), CircleShape(_)) =>
        resolveCircleVsRect(b, a)

      case _ =>
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

    val CircleShape(ar) = a.shape
    val CircleShape(br) = b.shape

    val dx = b.pos.x - a.pos.x
    val dy = b.pos.y - a.pos.y
    val distSq = dx * dx + dy * dy

    val minDist = ar + br
    if (distSq >= minDist * minDist) return

    // events
    (a, b) match {
      case (ability: AbilityBody, creature: CreatureBody) =>
        game.queues.localEventQueue.enqueue(
          ProjectileComponentHitsCreatureEvent(
            creature.creatureId,
            ability.projectileComponentId,
            ability.areaId
          )
        )

      case (creature: CreatureBody, ability: AbilityBody) =>
        game.queues.localEventQueue.enqueue(
          ProjectileComponentHitsCreatureEvent(
            creature.creatureId,
            ability.projectileComponentId,
            ability.areaId
          )
        )

      case _ =>
    }

    if (a.isSensor || b.isSensor) return

    val dist = Math.sqrt(distSq).toFloat
    val (nx, ny) = if (dist == 0f) (1f, 0f) else (dx / dist, dy / dist)
    val overlap = minDist - dist

    if (!a.isPushable && !b.isPushable) {} else if (!a.isPushable)
      b.setPos(Vector2f(b.pos.x + nx * overlap, b.pos.y + ny * overlap))
    else if (!b.isPushable)
      a.setPos(Vector2f(a.pos.x - nx * overlap, a.pos.y - ny * overlap))
    else {
      val half = overlap * 0.5f
      a.setPos(Vector2f(a.pos.x - nx * half, a.pos.y - ny * half))
      b.setPos(Vector2f(b.pos.x + nx * half, b.pos.y + ny * half))
    }
  }

  private def resolveCircleVsRect(circle: PhysicsBody, rect: PhysicsBody)(implicit
      game: CoreGame
  ): Unit = {

    val CircleShape(radius) = circle.shape
    val RectShape(w, h) = rect.shape

    val hw = w * 0.5f
    val hh = h * 0.5f

    val cx = circle.pos.x
    val cy = circle.pos.y

    val rx = rect.pos.x
    val ry = rect.pos.y

    val closestX = clamp(cx, rx - hw, rx + hw)
    val closestY = clamp(cy, ry - hh, ry + hh)

    val dx = cx - closestX
    val dy = cy - closestY
    val distSq = dx * dx + dy * dy

    if (distSq >= radius * radius) return

    // event
    circle match {
      case ac: AbilityBody =>
        game.queues.localEventQueue.enqueue(
          ProjectileComponentHitsTerrainEvent(
            ac.projectileComponentId,
            ac.areaId
          )
        )
      case _ =>
    }

    if (circle.isSensor) return

    val dist = Math.sqrt(distSq).toFloat

    if (dist == 0f) {
      val left = cx - (rx - hw)
      val right = (rx + hw) - cx
      val down = cy - (ry - hh)
      val up = (ry + hh) - cy
      val min = Math.min(Math.min(left, right), Math.min(down, up))

      if (min == left) circle.setPos(Vector2f(rx - hw - radius, cy))
      else if (min == right) circle.setPos(Vector2f(rx + hw + radius, cy))
      else if (min == down) circle.setPos(Vector2f(cx, ry - hh - radius))
      else circle.setPos(Vector2f(cx, ry + hh + radius))
      return
    }

    val nx = dx / dist
    val ny = dy / dist
    val overlap = radius - dist

    circle.setPos(Vector2f(cx + nx * overlap, cy + ny * overlap))
  }

  private def clamp(v: Float, min: Float, max: Float): Float =
    Math.max(min, Math.min(v, max))
}
