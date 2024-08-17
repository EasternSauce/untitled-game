package com.easternsauce.game

case class SpriteBatches(
                          worldSpriteBatch: GameSpriteBatch = GameSpriteBatch(),
                          worldTextSpriteBatch: GameSpriteBatch = GameSpriteBatch(),
                          hudBatch: GameSpriteBatch = GameSpriteBatch()
)
