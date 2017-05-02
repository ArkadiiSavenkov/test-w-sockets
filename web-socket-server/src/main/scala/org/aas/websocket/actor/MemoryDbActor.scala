package org.aas.websocket.actor

import akka.actor.Actor
import org.aas.websocket.model._

import scalaz.{-\/, \/-}

class MemoryDbActor extends Actor {
  val list: ListTableBuffer = new ListTableBuffer

  override def receive: Receive = {
    case AddTableRequest(afterId, tableWithoutId) =>
      val table = list.insert(afterId, tableWithoutId)
      sender() ! TableAddedEvent(afterId, table)

    case UpdateTableRequest(requestTable) =>
      list.update(requestTable) match {
        case -\/(id) => sender() ! UpdateFailedResponse(id)
        case \/-(table) => sender() ! TableUpdatedEvent(table)
      }

    case RemoveTableRequest(tableId) =>
      list.removeTable(tableId) match {
        case -\/(id) => sender() ! RemovalFailedResponse(id)
        case \/-(table) => sender() ! TableRemovedEvent(tableId)
      }

    case _: SubscribeTablesRequest =>
      sender() ! TableListResponse(list.all)
  }
}
