package com.easternsauce.game.command

case class RegisterClientRequestCommand(clientId: Option[String])
    extends GameCommand
