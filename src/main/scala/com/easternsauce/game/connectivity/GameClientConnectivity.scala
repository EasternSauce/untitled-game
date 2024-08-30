package com.easternsauce.game.connectivity
import com.easternsauce.game.Constants
import com.easternsauce.game.client.CoreGameClient
import com.esotericsoftware.kryonet.{Client, KryoSerialization, Listener}
import com.twitter.chill.{Kryo, ScalaKryoInstantiator}

case class GameClientConnectivity(game: CoreGameClient)
    extends GameConnectivity {

  override val endPoint: Client = {
    if (!Constants.OfflineMode) {
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
