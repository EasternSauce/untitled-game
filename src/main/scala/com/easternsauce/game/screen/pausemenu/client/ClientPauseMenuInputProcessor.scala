package com.easternsauce.game.screen.pausemenu.client

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputProcessor
import com.easternsauce.game.core.CoreGame

case class ClientPauseMenuInputProcessor()(implicit game: CoreGame)
    extends InputProcessor {
  override def keyDown(keycode: Int): Boolean = {
    keycode match {
      case Keys.ESCAPE => game.setGameplayScreen()
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
    false
  }

  override def touchUp(
      screenX: Int,
      screenY: Int,
      pointer: Int,
      button: Int
  ): Boolean = {
    false
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
