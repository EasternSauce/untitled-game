package com.easternsauce.game.server

import com.easternsauce.game.Constants
import com.easternsauce.game.command.OverrideGameStateCommand
import com.esotericsoftware.kryonet.{Connection, Server}

case class GameStateBroadcaster(game: CoreGameServer) {
  def stop(): Unit = {
    broadcastThread.interrupt()
  }

  private var broadcastThread: Thread = _

  def start(endPoint: Server): Unit = {
    broadcastThread = createBroadcastThread(endPoint)
    broadcastThread.start()
  }

  private def createBroadcastThread(endPoint: Server): Thread = new Thread {
    override def run(): Unit = {
      try while ({
        true
      }) {
        Thread.sleep(
          (Constants.TimeBetweenGameStateBroadcasts * 1000f).toInt
        )
        val connections = endPoint.getConnections
        for (connection <- connections) {
          broadcastToConnection(connection)
        }
      } catch {
        case _: InterruptedException =>

        // do nothing
      }
    }
  }

  private def broadcastToConnection(connection: Connection): Unit = {
    connection.sendTCP(OverrideGameStateCommand(game.gameState))
  }
}
