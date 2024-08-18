package com.easternsauce.game

import com.badlogic.gdx.Gdx
import com.easternsauce.game.math.{IsometricProjection, Vector2f}

object GameInput {
  def mouseWorldPos(
      screenX: Float,
      screenY: Float,
      playerPos: Vector2f
  ): Vector2f = {
    val mouseScreenPos =
      IsometricProjection.translatePosScreenToIso(
        mousePosByViewport(screenX, screenY)
      )

    playerPos.add(mouseScreenPos)
  }

  private def mousePosByViewport(screenX: Float, screenY: Float): Vector2f = {
    val mouseX =
      screenX * Constants.ViewportWorldWidth / Gdx.graphics.getWidth
    val mouseY =
      Constants.ViewportWorldHeight - (screenY * Constants.ViewportWorldHeight / Gdx.graphics.getHeight)

    val mouseCenterX = mouseX - Constants.ViewportWorldWidth / 2f
    val mouseCenterY = mouseY - Constants.ViewportWorldHeight / 2f

    Vector2f(mouseCenterX, mouseCenterY)
  }
}
