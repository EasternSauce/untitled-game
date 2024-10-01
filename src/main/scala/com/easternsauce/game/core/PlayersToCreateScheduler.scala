package com.easternsauce.game.core

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class PlayersToCreateScheduler() {
  private var _playersToCreate: mutable.ListBuffer[String] = _

  def init(): Unit = {
    _playersToCreate = ListBuffer()
  }

  def playersToCreate: List[String] = {
    _playersToCreate.toList
  }

  def clearPlayersToCreate(): Unit = {
    _playersToCreate.clear()
  }

  def schedulePlayerToCreate(clientId: String): Unit = {
    _playersToCreate += clientId
  }

}
