package org.aas.websocket.graph

import akka.actor.ActorRef
import akka.stream.scaladsl.Flow
import org.aas.websocket.model._

import scala.concurrent.Future

class MemoryDb(ref: ActorRef) {

  import akka.pattern.ask

  def flow = {
    Flow[Model].mapAsync(5) { m =>

      Future((m match {
        case model: AddTableRequest => ref ? model
        case model: UpdateTableRequest => ref ? model
        case model: RemoveTableRequest => ref ? model
        case model: SubscribeTablesRequest => ref ? model
        case _ => m
      }).asInstanceOf[Model])
    }
  }
}