package com.easternsauce.game.gamephysics

import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamemap.GameMapCell

case class StaticBodyFactory() {

  /** Create StaticObjectBody for each cell */
  def createStaticObjectBodies(cells: List[GameMapCell], world: AreaWorld)(implicit
      game: CoreGame
  ): List[StaticObjectBody] = {
    cells.map(_.pos()).distinct.map { pos =>
      val body = StaticObjectBody(s"staticObject_${pos.x}_${pos.y}")
      body.init(world, pos)
      body
    }
  }
}
