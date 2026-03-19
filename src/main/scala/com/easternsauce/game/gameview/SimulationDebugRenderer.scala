package com.easternsauce.game.gameview

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.easternsauce.game.core.CoreGame
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
      if (body.isStatic) {
        shapeRenderer.setColor(Color.YELLOW)
        shapeRenderer.rect(
          body.pos.x - body.radius,
          body.pos.y - body.radius,
          body.radius * 2,
          body.radius * 2
        )
      } else {
        shapeRenderer.setColor(Color.RED)
        shapeRenderer.circle(
          body.pos.x,
          body.pos.y,
          body.radius,
          16
        )
      }
    }

    shapeRenderer.end()
  }
}
