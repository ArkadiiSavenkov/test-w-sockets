package org.aas.websocket.actor

import org.aas.websocket.model._

import scala.collection.mutable.ArrayBuffer
import scalaz.{-\/, \/, \/-}

private[actor] class ListTableBuffer {
  private var list: ArrayBuffer[Table] = ArrayBuffer()
  private var genetatedId: Long = 0

  def insert(afterId: Long, tableWithoutId: TableWithoutId) = {
    val newTable = Table(genetatedId, tableWithoutId.name, tableWithoutId.participants)
    genetatedId += 1

    list.size match {
      case 0 => list.append(newTable)
      case _ => list.insert(list.indexWhere(t => t.id == afterId) + 1, newTable)
    }
    newTable
  }

  def update(requestTable: Table): Long \/ Table = {
    list.indexWhere(t => t.id == requestTable.id) match {
      case -1 => -\/(requestTable.id)
      case findIndex =>
        list.update(findIndex, requestTable)
        \/-(requestTable)
    }

  }

  def removeTable(tableId: Long): Long \/ Table = {
    list.indexWhere(t => t.id == tableId) match {
      case -1 => -\/(tableId)
      case findIndex =>
        \/-(list.remove(findIndex))
    }

  }

  def all: List[Table] = {
    list.toList
  }

}
