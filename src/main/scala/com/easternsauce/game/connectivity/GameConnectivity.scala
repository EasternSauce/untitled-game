package com.easternsauce.game.connectivity

import com.esotericsoftware.kryonet.EndPoint
import com.esotericsoftware.kryonet.Listener

trait GameConnectivity {
  val endPoint: EndPoint
  val listener: Listener
}
