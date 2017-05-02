package org.aas.websocket.graph

import akka.NotUsed
import akka.actor.ActorRef
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import org.aas.websocket.model._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


class MemoryDb(ref: ActorRef) {

  import akka.pattern.ask

  implicit val askTimeout = Timeout(30 seconds)

  def flow: Flow[Model, Model, NotUsed] = {
    Flow[Model].mapAsync(5) { m =>
      m match {
        case addTableRequest: AddTableRequest => (ref ? addTableRequest).map(_.asInstanceOf[Model])
        case updateTableRequest: UpdateTableRequest => (ref ? updateTableRequest).map(_.asInstanceOf[Model])
        case removeTableRequest: RemoveTableRequest => (ref ? removeTableRequest).map(_.asInstanceOf[Model])
        case _ => Future(m)
      }
    }.map { m =>
      println(s"MemoryDb -> $m")
      m
    }
  }
}

object MemoryDb {
  def apply(ref: ActorRef) = new MemoryDb(ref)
}