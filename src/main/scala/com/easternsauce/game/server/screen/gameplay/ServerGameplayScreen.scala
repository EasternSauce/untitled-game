package com.easternsauce.game.server.screen.gameplay

import com.badlogic.gdx.Screen
import com.easternsauce.game.CoreGame

case class ServerGameplayScreen(game: CoreGame) extends Screen {
  override def show(): Unit = {}

  override def render(delta: Float): Unit = {}

  override def resize(width: Int, height: Int): Unit = {}

  override def pause(): Unit = {}

  override def resume(): Unit = {}

  override def hide(): Unit = {}

  override def dispose(): Unit = {}
}
