package org.aas.websocket.graph

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

  def flow = {
    Flow[Model].mapAsync(5) { m =>

      Future((m match {
        case addTableRequest: AddTableRequest => ref ? addTableRequest
        case updateTableRequest: UpdateTableRequest => ref ? updateTableRequest
        case removeTableRequest: RemoveTableRequest => ref ? removeTableRequest
        case subscribeTablesRequest: SubscribeTablesRequest => ref ? subscribeTablesRequest
        case _ => m
      }).asInstanceOf[Model])
    }
  }
}

object MemoryDb {
  def apply(ref: ActorRef) = new MemoryDb(ref)
}