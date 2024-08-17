package com.easternsauce.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture

object Assets {
  private var textures: Map[String, Texture] = Map[String, Texture]()

  def getTexture(texturePath: String): Texture = {
    if (textures.contains(texturePath)) {
      textures(texturePath)
    } else {
      textures = textures.updated(
        texturePath,
        new Texture(Gdx.files.internal("assets/" + texturePath + ".png"))
      )
      textures(texturePath)
    }
  }
}
