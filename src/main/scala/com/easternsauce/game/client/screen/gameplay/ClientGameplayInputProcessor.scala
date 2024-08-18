package com.easternsauce.game.client.screen.gameplay

import com.badlogic.gdx.Input.{Buttons, Keys}
import com.badlogic.gdx.InputProcessor
import com.easternsauce.game.{CoreGame, GameInput}
import com.softwaremill.quicklens.{ModifyPimp, QuicklensMapAt}

case class ClientGameplayInputProcessor(game: CoreGame) extends InputProcessor {
  override def keyDown(keycode: Int): Boolean = {
    keycode match {
      case Keys.Z      => println("typed Z!")
      case Keys.ESCAPE => game.pauseGame()
      case _           =>
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
        val creature = game.clientCreatureId.map(game.gameState.creatures(_))

        creature.foreach(creature => {
          val vectorTowardsDestination =
            creature.pos.vectorTowards(creature.params.destination)

          val destination = GameInput.mouseWorldPos(
            screenX,
            screenY,
            creature.pos
          )

          game.gameState = game.gameState
            .modify(_.creatures.at(creature.id))
            .using(
              _.modify(_.params.destination)
                .setTo(destination)
            )
            .modify(_.creatures.at(creature.id).params.facingVector)
            .setToIf(vectorTowardsDestination.length > 0)(
              vectorTowardsDestination
            )
        })

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
