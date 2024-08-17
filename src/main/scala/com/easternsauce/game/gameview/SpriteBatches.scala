package com.easternsauce.game.gameview

case class SpriteBatches(
    worldSpriteBatch: GameSpriteBatch = GameSpriteBatch(),
    worldTextSpriteBatch: GameSpriteBatch = GameSpriteBatch(),
    hudBatch: GameSpriteBatch = GameSpriteBatch()
)
