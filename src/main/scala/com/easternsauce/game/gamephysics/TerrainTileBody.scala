package com.easternsauce.game.gamephysics

import com.badlogic.gdx.physics.box2d.BodyDef
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.math.Vector2f

case class TerrainTileBody(terrainId: String) extends PhysicsBody {

  override def init(areaWorld: AreaWorld, pos: Vector2f)(implicit game: CoreGame): Unit = {
    this.b2Body = new BodyFactory(areaWorld)
      .withType(BodyDef.BodyType.StaticBody)
      .at(Vector2f(pos.x + 0.5f, pos.y + 0.5f))
      .withBox(0.5f, 0.5f)
      .withUserData(this)
      .build()

    this.areaWorld = areaWorld
    this.sensor = false
  }

  override protected def radius(implicit game: CoreGame): Float = 0f
  override protected def velocity(gameState: GameState): Option[Vector2f] = None
  override def update(gameState: GameState): Unit = {}
  override def onRemove(): Unit = {}
}
