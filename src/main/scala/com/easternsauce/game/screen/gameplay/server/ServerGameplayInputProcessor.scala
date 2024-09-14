package com.easternsauce.game.screen.gameplay.server

import com.badlogic.gdx.InputProcessor
import com.easternsauce.game.CoreGame

case class ServerGameplayInputProcessor()(implicit game: CoreGame)
    extends InputProcessor {

  override def keyDown(keycode: Int): Boolean = {
    keycode match {
      case keycode =>
        game.gameplay.setKeyHeld(keycode, value = true)
    }

    keycode match {
      case _ => false
    }
  }

  override def keyUp(keycode: Int): Boolean = {
    keycode match {
      case keycode =>
        game.gameplay.setKeyHeld(keycode, value = false)
    }
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
      case button =>
        game.gameplay.setKeyHeld(button, value = true)
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
      case button =>
        game.gameplay.setKeyHeld(button, value = false)
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
