package com.easternsauce.game.gameview

case class SpriteBatchHolder(
    var worldSpriteBatch: GameSpriteBatch = GameSpriteBatch(),
    var worldTextSpriteBatch: GameSpriteBatch = GameSpriteBatch(),
    var hudSpriteBatch: GameSpriteBatch = GameSpriteBatch()
) {

  def init(): Unit = {
    worldSpriteBatch.init()
    worldTextSpriteBatch.init()
    hudSpriteBatch.init()
  }

}
