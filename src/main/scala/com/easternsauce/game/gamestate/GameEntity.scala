package com.easternsauce.game.gamestate

import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.util.TransformIf

/** All game entities now carry their current area */
trait GameEntity extends TransformIf {
  def currentAreaId: AreaId
}
