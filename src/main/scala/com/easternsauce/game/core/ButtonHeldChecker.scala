package com.easternsauce.game.core

import scala.collection.mutable

case class ButtonHeldChecker() {
  private var buttonsHeld: mutable.Map[Int, Boolean] = _

  def init(): Unit = {
    buttonsHeld = mutable.Map()
  }

  def isButtonHeld(key: Int): Boolean = buttonsHeld.getOrElse(key, false)

  def setButtonHeld(key: Int, value: Boolean): Unit =
    buttonsHeld.update(key, value)
}
