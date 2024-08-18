package com.easternsauce.game.client.screen.gameplay

import com.badlogic.gdx.Input.{Buttons, Keys}
import com.badlogic.gdx.InputProcessor
import com.easternsauce.game.gamestate.id.GameEntityId
import com.easternsauce.game.{CoreGame, GameInput}
import com.softwaremill.quicklens.{ModifyPimp, QuicklensMapAt}

case class ClientGameplayInputProcessor(game: CoreGame) extends InputProcessor {
  override def keyDown(keycode: Int): Boolean = {
    keycode match {
      case Keys.Z      => println("typed Z!")
      case Keys.ESCAPE => game.pauseGame()
      case Keys.W =>
        game.gameState = game.gameState.modify(_.cameraPos.x).using(_ - 10)
      case Keys.S =>
        game.gameState = game.gameState.modify(_.cameraPos.x).using(_ + 10)
      case Keys.A =>
        game.gameState = game.gameState.modify(_.cameraPos.y).using(_ - 10)
      case Keys.D =>
        game.gameState = game.gameState.modify(_.cameraPos.y).using(_ + 10)
      case _ =>
    }
    true
  }

  override def keyUp(keycode: Int): Boolean = {
    false
  }

  override def keyTyped(character: Char): Boolean = {
    false
  }

  override def touchDown(
      screenX: Int,
      screenY: Int,
      pointer: Int,
      button: Int
  ): Boolean = {
    button match {
      case Buttons.LEFT =>
        val creature = game.gameState.creatures(GameEntityId("player"))

        val vectorTowardsDestination =
          creature.pos.vectorTowards(creature.params.destination)

        val destination = GameInput.mouseWorldPos(
          screenX,
          screenY,
          game.gameState.creatures(GameEntityId("player")).pos
        )

        println("moving towards " + destination)

        game.gameState = game.gameState
          .modify(_.creatures.at(GameEntityId("player")))
          .using(
            _.modify(_.params.destination)
              .setTo(destination)
          )
          .modify(_.creatures.at(GameEntityId("player")).params.facingVector)
          .setToIf(vectorTowardsDestination.length > 0)(
            vectorTowardsDestination
          )

        true
    }
  }

  override def touchUp(
      screenX: Int,
      screenY: Int,
      pointer: Int,
      button: Int
  ): Boolean = {
    false
  }

  override def touchCancelled(
      screenX: Int,
      screenY: Int,
      pointer: Int,
      button: Int
  ): Boolean = {
    false
  }

  override def touchDragged(
      screenX: Int,
      screenY: Int,
      pointer: Int
  ): Boolean = {
    false
  }

  override def mouseMoved(screenX: Int, screenY: Int): Boolean = {
    false
  }

  override def scrolled(amountX: Float, amountY: Float): Boolean = {
    false
  }
}
