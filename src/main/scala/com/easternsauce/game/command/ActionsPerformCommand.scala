package com.easternsauce.game.command

import com.easternsauce.game.gamestate.event.GameStateEvent

case class ActionsPerformCommand(actions: List[GameStateEvent])
    extends GameCommand
