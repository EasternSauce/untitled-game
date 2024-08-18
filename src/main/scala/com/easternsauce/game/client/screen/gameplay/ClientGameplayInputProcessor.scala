package com.easternsauce.game.client.screen.gameplay

import com.badlogic.gdx.Input.{Buttons, Keys}
import com.badlogic.gdx.InputProcessor
import com.easternsauce.game.CoreGame

case class ClientGameplayInputProcessor(game: CoreGame) extends InputProcessor {
  override def keyDown(keycode: Int): Boolean = {
    keycode match {
      case Keys.ESCAPE => game.pauseGame()
      case _           =>
    }
    true
  }

  override def keyUp(keycode: Int): Boolean = {
    false
  }

  override def keyTyped(character: Char): Boolean = {
    false
  }

  override def touchDown(
      screenX: Int,
      screenY: Int,
      pointer: Int,
      button: Int
  ): Boolean = {
    button match {
      case Buttons.LEFT =>
        game.holdButtonInput.mouseLeftButton = true
      case _ =>
    }
    true
  }

  override def touchUp(
      screenX: Int,
      screenY: Int,
      pointer: Int,
      button: Int
  ): Boolean = {
    button match {
      case Buttons.LEFT =>
        game.holdButtonInput.mouseLeftButton = false
      case _ =>
    }
    true
  }

  override def touchCancelled(
      screenX: Int,
      screenY: Int,
      pointer: Int,
      button: Int
  ): Boolean = {
    false
  }

  override def touchDragged(
      screenX: Int,
      screenY: Int,
      pointer: Int
  ): Boolean = {
    false
  }

  override def mouseMoved(screenX: Int, screenY: Int): Boolean = {
    false
  }

  override def scrolled(amountX: Float, amountY: Float): Boolean = {
    false
  }
}
