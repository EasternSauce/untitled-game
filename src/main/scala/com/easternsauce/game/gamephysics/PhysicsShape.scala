package com.easternsauce.game.gamephysics

sealed trait PhysicsShape

case class CircleShape(radius: Float) extends PhysicsShape
case class RectShape(width: Float, height: Float) extends PhysicsShape
