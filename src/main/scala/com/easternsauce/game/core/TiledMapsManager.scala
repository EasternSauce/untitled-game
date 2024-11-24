package com.easternsauce.game.core

import com.easternsauce.game.Constants
import com.easternsauce.game.gamemap.GameTiledMap
import com.easternsauce.game.gamestate.id.AreaId

import scala.collection.mutable

case class TiledMapsManager() {
  var tiledMaps: mutable.Map[AreaId, GameTiledMap] = _

  def init(): Unit = {
    tiledMaps = mutable.Map() ++ Constants.MapAreaNames
      .map(name => (AreaId(name), GameTiledMap(AreaId(name))))
      .toMap
    tiledMaps.values.foreach(_.init())
  }

}
