package com.easternsauce.game.command

import com.easternsauce.game.gamestate.GameState
import com.easternsauce.game.gamestate.event.GameStateEvent

sealed trait GameCommand

case class ActionsPerformRequestCommand(actions: List[GameStateEvent])
    extends GameCommand

case class ActionsPerformCommand(actions: List[GameStateEvent])
    extends GameCommand

case class OverrideGameStateCommand(gameState: GameState) extends GameCommand

case class RegisterClientRequestCommand(clientId: Option[String])
    extends GameCommand

case class RegisterClientResponseCommand(clientId: String) extends GameCommand
