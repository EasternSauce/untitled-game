package com.easternsauce.game

case class ClientData(
    var clientId: Option[String] = None,
    var host: Option[String] = None,
    var port: Option[String] = None
) {}
