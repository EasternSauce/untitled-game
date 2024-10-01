package com.easternsauce.game.core

import scala.collection.mutable

case class KeyHeldChecker() {
  private var keysHeld: mutable.Map[Int, Boolean] = _

  def init(): Unit = {
    keysHeld = mutable.Map()
  }

  def keyHeld(key: Int): Boolean = keysHeld.getOrElse(key, false)

  def setKeyHeld(key: Int, value: Boolean): Unit =
    keysHeld.update(key, value)
}
