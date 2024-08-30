package com.easternsauce.game.connectivity
import com.easternsauce.game.server.CoreGameServer
import com.esotericsoftware.kryonet.{KryoSerialization, Listener, Server}
import com.twitter.chill.{Kryo, ScalaKryoInstantiator}

case class GameServerConnectivity(game: CoreGameServer)
    extends GameConnectivity {

  override val endPoint: Server = {
    val kryo: Kryo = {
      val instantiator = new ScalaKryoInstantiator
      instantiator.setRegistrationRequired(false)
      instantiator.newKryo()
    }

    new Server(16384 * 100, 2048 * 100, new KryoSerialization(kryo))
  }

  override val listener: Listener = ServerListener(game)
}
