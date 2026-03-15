package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.math.Vector2f

import scala.collection.mutable

case class AreaWorld(areaId: AreaId) {

  // ===== CONFIG =====
  private val chunkSize = 2f

  // ===== STORAGE =====
  private val staticChunks =
    mutable.Map[(Int, Int), mutable.Set[PhysicsBody]]()

  private val dynamicChunks =
    mutable.Map[(Int, Int), mutable.Set[PhysicsBody]]()

  private val staticBodies =
    mutable.Set[PhysicsBody]()

  private val dynamicBodies =
    mutable.Set[PhysicsBody]()

  // ===== PUBLIC API =====

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

  // Call ONCE after static world initialization
  def buildStaticChunks(): Unit = {
    staticChunks.clear()

    staticBodies.foreach { body =>
      val chunk = chunkOf(body.pos)
      val set = staticChunks.getOrElseUpdate(chunk, mutable.Set())
      set.add(body)
    }
  }

  // ===== UPDATE =====

  def update()(implicit game: CoreGame): Unit = {

    // Rebuild dynamic chunks every frame
    dynamicChunks.clear()

    dynamicBodies.foreach { body =>
      val chunk = chunkOf(body.pos)
      val set = dynamicChunks.getOrElseUpdate(chunk, mutable.Set())
      set.add(body)
    }

    // Collision check only within nearby chunks
    dynamicBodies.foreach { body =>
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
  }

  // ===== CHUNK HELPERS =====

  private def chunkOf(pos: Vector2f): (Int, Int) = {
    val cx = Math.floor(pos.x / chunkSize).toInt
    val cy = Math.floor(pos.y / chunkSize).toInt
    (cx, cy)
  }

  // ===== COLLISION =====

  private def resolveCollision(a: PhysicsBody, b: PhysicsBody)(implicit game: CoreGame): Unit = {

    val ax = a.pos.x
    val ay = a.pos.y
    val bx = b.pos.x
    val by = b.pos.y

    val dx = bx - ax
    val dy = by - ay

    val distSq = dx * dx + dy * dy
    val minDist = a.radius + b.radius
    val minDistSq = minDist * minDist

    if (distSq >= minDistSq || distSq == 0f) return

    val dist = Math.sqrt(distSq).toFloat
    val overlap = minDist - dist

    val nx = dx / dist
    val ny = dy / dist

    if (a.isStatic && b.isStatic) return

    if (a.isStatic) {
      b.setPos(
        Vector2f(
          bx + nx * overlap,
          by + ny * overlap
        )
      )
    } else if (b.isStatic) {
      a.setPos(
        Vector2f(
          ax - nx * overlap,
          ay - ny * overlap
        )
      )
    } else {
      val half = overlap * 0.5f

      a.setPos(
        Vector2f(
          ax - nx * half,
          ay - ny * half
        )
      )

      b.setPos(
        Vector2f(
          bx + nx * half,
          by + ny * half
        )
      )
    }
  }
}
