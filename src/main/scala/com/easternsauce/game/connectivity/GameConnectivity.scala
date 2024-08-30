package com.easternsauce.game.connectivity

import com.esotericsoftware.kryonet.{EndPoint, Listener}

trait GameConnectivity {
  val endPoint: EndPoint
  val listener: Listener
}
