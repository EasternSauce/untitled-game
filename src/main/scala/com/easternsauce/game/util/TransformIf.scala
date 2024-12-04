package com.easternsauce.game.util

trait TransformIf {
  def transformIf[T](cond: => Boolean)(codeBlock: => T): T = {
    if (cond) {
      codeBlock
    } else {
      this.asInstanceOf[T]
    }
  }
}
