package com.easternsauce.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture

import scala.collection.mutable

object Assets {
  private val textures: mutable.Map[String, Texture] =
    mutable.Map[String, Texture]()

  def texture(texturePath: String): Texture = {
    if (textures.contains(texturePath)) {
      textures(texturePath)
    } else {
      textures.update(
        texturePath,
        new Texture(Gdx.files.internal("assets/" + texturePath + ".png"))
      )
      textures(texturePath)
    }
  }
}
