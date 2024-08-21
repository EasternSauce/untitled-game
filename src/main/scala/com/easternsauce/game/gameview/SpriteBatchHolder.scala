package com.easternsauce.game.gameview

case class SpriteBatchHolder() {
  var worldSpriteBatch: GameSpriteBatch = _
  var worldTextSpriteBatch: GameSpriteBatch = _
  var hudBatch: GameSpriteBatch = _

  def init(): Unit = {
    worldSpriteBatch = GameSpriteBatch()
    worldSpriteBatch.init()

    worldTextSpriteBatch = GameSpriteBatch()
    worldTextSpriteBatch.init()

    hudBatch = GameSpriteBatch()
    hudBatch.init()
  }

}
