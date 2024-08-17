package com.easternsauce.game.gamemap

import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer, TmxMapLoader}
import com.easternsauce.game.Constants
import com.easternsauce.game.gamestate.{AreaId, GameState}
import com.easternsauce.game.gameview.GameSpriteBatch
import com.easternsauce.game.math.Vector2f

case class GameMapRenderer(areaId: AreaId) {
  private var tiledMap: TiledMap = _

  private var layers: Map[String, GameMapLayer] = _

  def init(): Unit = {
    val params = new TmxMapLoader.Parameters()

    tiledMap = new TmxMapLoader()
      .load(s"assets/maps/${areaId.seed}/${areaId.seed}.tmx", params)

    val iterator = tiledMap.getLayers.iterator()

    var layerNames: List[String] = List()

    while (iterator.hasNext) {
      layerNames = layerNames.appended(iterator.next().getName)
    }

    layers = layerNames.reverse
      .map(layerName => (layerName, loadLayer(layerName)))
      .toMap
  }

  private def loadLayer(layerName: String): GameMapLayer = {
    val layer: TiledMapTileLayer =
      tiledMap.getLayers.get(layerName).asInstanceOf[TiledMapTileLayer]

    val cells: List[Option[GameMapCell]] = for {
      x <- (0 until layer.getWidth).toList
      y <- (0 until layer.getHeight).reverse
    } yield {
      Option(layer.getCell(x, y)).map(GameMapCell(_, areaId, Vector2f(x, y)))
    }

    GameMapLayer(layerName, cells.flatten)
  }

  def render(
      batch: GameSpriteBatch,
      worldCameraPos: Vector2f,
      gameState: GameState
  ): Unit = {
    for {
      layer <- Constants.LayersByRenderingOrder.flatMap(layers.get(_))
      cell <- layer.cells
    } yield {
      cell.render(batch, worldCameraPos, gameState)
    }
  }
}
