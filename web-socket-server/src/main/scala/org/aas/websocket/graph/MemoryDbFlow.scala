package org.aas.websocket.graph

import akka.NotUsed
import akka.actor.ActorRef
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import org.aas.websocket.model._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


class MemoryDbFlow(ref: ActorRef) {

  import akka.pattern.ask

  implicit val askTimeout = Timeout(30 seconds)

  def flow: Flow[Parcel, Parcel, NotUsed] = {
    Flow[Parcel].mapAsync(5) {
      case addTableRequest: AddTableRequest => (ref ? addTableRequest).map(_.asInstanceOf[Parcel])
      case updateTableRequest: UpdateTableRequest => (ref ? updateTableRequest).map(_.asInstanceOf[Parcel])
      case removeTableRequest: RemoveTableRequest => (ref ? removeTableRequest).map(_.asInstanceOf[Parcel])
      case m => Future(m)
    }
  }
}

object MemoryDbFlow {
  def apply(ref: ActorRef) = new MemoryDbFlow(ref)
}