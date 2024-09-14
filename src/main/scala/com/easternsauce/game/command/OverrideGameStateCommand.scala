package com.easternsauce.game.command

import com.easternsauce.game.gamestate.GameState

case class OverrideGameStateCommand(gameState: GameState) extends GameCommand
