package com.easternsauce.game.gamemap

import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer, TmxMapLoader}
import com.easternsauce.game.Constants
import com.easternsauce.game.core.CoreGame
import com.easternsauce.game.gamestate.id.AreaId
import com.easternsauce.game.gameview.GameSpriteBatch
import com.easternsauce.game.math.Vector2f

case class GameTiledMap(areaId: AreaId) {
  private var tiledMap: TiledMap = _

  private var layers: Map[String, GameMapLayer] = _

  def init(): Unit = {
    val params = new TmxMapLoader.Parameters()

    tiledMap = new TmxMapLoader()
      .load(
        s"${Constants.MapFilesLocation}/${areaId.name}/${areaId.name}.tmx",
        params
      )

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
      Option(layer.getCell(x, y)).map(
        GameMapCell(_, areaId, Vector2f(x.toFloat, y.toFloat))
      )
    }

    GameMapLayer(layerName, cells.flatten)
  }

  def layerWidth(layerName: String): Float = tiledMap.getLayers
    .get(layerName)
    .asInstanceOf[TiledMapTileLayer]
    .getWidth * Constants.TileSize
  def layerHeight(layerName: String): Float = tiledMap.getLayers
    .get(layerName)
    .asInstanceOf[TiledMapTileLayer]
    .getHeight * Constants.TileSize

  def renderBottomLayers(batch: GameSpriteBatch, worldCameraPos: Vector2f)(
      implicit game: CoreGame
  ): Unit = {
    for {
      layer <- Constants.BottomLayers.flatMap(layers.get(_))
      cell <- layer.cells
    } yield {
      cell.render(batch, worldCameraPos)
    }
  }

  def getDynamicLayerCells()(implicit game: CoreGame): List[GameMapCell] = {
    for {
      layer <- Constants.DynamicLayers.flatMap(layers.get(_))
      cell <- layer.cells
    } yield {
      cell
    }
  }

  def renderTopLayers(batch: GameSpriteBatch, worldCameraPos: Vector2f)(implicit
      game: CoreGame
  ): Unit = {
    for {
      layer <- Constants.TopLayers.flatMap(layers.get(_))
      cell <- layer.cells
    } yield {
      cell.render(batch, worldCameraPos)
    }
  }

  def layer(name: String): GameMapLayer = layers(name)
}
