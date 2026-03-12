package com.easternsauce.game.gamephysics

import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gameview.GameViewport

case class AreaWorld(areaId: AreaId) {

  def init(): Unit = {}

  def renderDebug(b2DebugViewport: GameViewport): Unit = {}

  def update(): Unit = {}

}
