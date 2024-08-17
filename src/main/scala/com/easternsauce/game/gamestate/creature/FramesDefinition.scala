package com.easternsauce.game.gamestate.creature

case class FramesDefinition(start: Int, count: Int, frameDuration: Float) {
  def totalDuration: Float = frameDuration * count
}
