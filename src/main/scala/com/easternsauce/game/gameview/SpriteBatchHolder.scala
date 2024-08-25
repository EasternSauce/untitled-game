package com.easternsauce.game.gameview

case class SpriteBatchHolder(
    var worldSpriteBatch: GameSpriteBatch = GameSpriteBatch(),
    var worldTextSpriteBatch: GameSpriteBatch = GameSpriteBatch(),
    var hudBatch: GameSpriteBatch = GameSpriteBatch()
) {

  def init(): Unit = {
    worldSpriteBatch.init()
    worldTextSpriteBatch.init()
    hudBatch.init()
  }

}
