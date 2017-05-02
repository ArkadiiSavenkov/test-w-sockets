package org.aas.websocket.actor

import akka.actor.Actor
import akka.actor.Actor.Receive
import org.aas.websocket.model._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class MemoryDbActor extends Actor {
  var list: ArrayBuffer[Table] = ArrayBuffer()
  var id: Long = 0

  override def receive: Receive = {
    case AddTableRequest(afterId, tableWithoutId) =>
      val newTable = Table(id, tableWithoutId.name, tableWithoutId.participants)
      id += 1
      list.insert(list.indexWhere(t => t.id == afterId), newTable)

    case UpdateTableRequest(requestTable) =>
      list.indexWhere(t => t.id == requestTable.id) match {
        case -1 => sender ! UpdateFailedResponse(requestTable.id)
        case findIndex =>
          list.update(findIndex, requestTable)
          sender() ! TableUpdatedEvent(requestTable)
      }

    case RemoveTableRequest(id) =>
      list.indexWhere(t => t.id == id) match {
        case -1 => sender() ! RemovalFailedResponse(id)
        case findIndex =>
          list.remove(findIndex)
          sender() ! TableRemovedEvent(id)
      }

    case _: SubscribeTablesRequest =>
      sender() ! TableListResponse(list.toList)
  }
}
