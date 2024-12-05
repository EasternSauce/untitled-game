package com.easternsauce.game.server

import com.badlogic.gdx.backends.lwjgl3.{
  Lwjgl3Application,
  Lwjgl3ApplicationConfiguration
}
import com.easternsauce.game.Constants

object GameServerLauncher {
  def main(arg: Array[String]): Unit = {
    val config = new Lwjgl3ApplicationConfiguration()

    config.setTitle("Drop")
    config.setWindowedMode(Constants.WindowWidth, Constants.WindowHeight)
    config.setForegroundFPS(120)
    config.setIdleFPS(120)

    new Lwjgl3Application(CoreGameServer(), config)
  }
}
