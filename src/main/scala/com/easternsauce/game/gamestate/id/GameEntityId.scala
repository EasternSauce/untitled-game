package com.easternsauce.game.gamestate.id

import com.easternsauce.game.gamestate.GameEntity

case class GameEntityId[T <: GameEntity](value: String) {
  override def toString: String = value
}

object GameEntityId {
  def apply[T <: GameEntity](value: String): GameEntityId[T] = new GameEntityId(
    value
  )
}
