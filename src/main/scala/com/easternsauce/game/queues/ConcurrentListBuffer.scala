package com.easternsauce.game.queues

import scala.collection.mutable.ListBuffer

class ConcurrentListBuffer[T] {
  private val listBuffer: ListBuffer[T] = ListBuffer()

  def +=(element: T): Unit = {
    listBuffer.synchronized {
      listBuffer += element
    }
  }

  def ++=(elements: IterableOnce[T]): Unit = {
    listBuffer.synchronized {
      listBuffer ++= elements
    }
  }

  def clear(): Unit = {
    listBuffer.synchronized {
      listBuffer.clear()
    }
  }

  def foldLeft[B](z: B)(op: (B, T) => B): B = {
    listBuffer.synchronized {
      listBuffer.foldLeft(z)(op)
    }
  }

  def toList: List[T] = {
    listBuffer.synchronized {
      listBuffer.toList
    }
  }
}

object ConcurrentListBuffer {
  def apply[T](): ConcurrentListBuffer[T] = new ConcurrentListBuffer[T]()
}
