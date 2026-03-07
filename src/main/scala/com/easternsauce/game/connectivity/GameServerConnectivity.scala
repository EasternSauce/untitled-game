package com.easternsauce.game.connectivity
import com.easternsauce.game.server.CoreGameServer
import com.esotericsoftware.kryonet.KryoSerialization
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server
import com.twitter.chill.Kryo
import com.twitter.chill.ScalaKryoInstantiator

case class GameServerConnectivity(game: CoreGameServer) extends GameConnectivity {

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
