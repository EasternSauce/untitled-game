package com.easternsauce.game.gameview

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamephysics.CircleShape
import com.easternsauce.game.gamephysics.RectShape
import com.easternsauce.game.gamephysics.WorldSimulation
import com.easternsauce.game.gamestate.id.AreaId

case class SimulationDebugRenderer() {

  private var shapeRenderer: ShapeRenderer = _
  private var viewportManager: CameraSystem = _

  def init(viewportManager: CameraSystem): Unit = {
    this.viewportManager = viewportManager
    shapeRenderer = new ShapeRenderer()
  }

  def render(
      areaId: AreaId,
      worldSimulation: WorldSimulation
  )(implicit game: CoreGame): Unit = {

    shapeRenderer.setProjectionMatrix(
      viewportManager.getB2DebugCombinedMatrix
    )

    shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

    worldSimulation.areaPhysicsWorlds(areaId).allBodies.foreach { body =>
      body.shape match {

        case CircleShape(r) =>
          shapeRenderer.setColor(Color.RED)
          shapeRenderer.circle(body.pos.x, body.pos.y, r, 16)

        case RectShape(w, h) =>
          shapeRenderer.setColor(Color.YELLOW)
          val hw = w * 0.5f
          val hh = h * 0.5f

          shapeRenderer.rect(
            body.pos.x - hw,
            body.pos.y - hh,
            w,
            h
          )
      }
    }

    shapeRenderer.end()
  }
}
