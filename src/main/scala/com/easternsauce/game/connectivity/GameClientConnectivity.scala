package com.easternsauce.game.connectivity
import com.easternsauce.game.client.CoreGameClientBase
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.KryoSerialization
import com.esotericsoftware.kryonet.Listener
import com.twitter.chill.Kryo
import com.twitter.chill.ScalaKryoInstantiator

case class GameClientConnectivity(game: CoreGameClientBase) extends GameConnectivity {

  override val endPoint: Client = {
    if (!game.isOffline) {
      val kryo: Kryo = {
        val instantiator = new ScalaKryoInstantiator
        instantiator.setRegistrationRequired(false)
        instantiator.newKryo()

      }
      new Client(8192 * 100, 2048 * 100, new KryoSerialization(kryo))
    } else {
      null
    }
  }

  override val listener: Listener = ClientListener(game)
}
