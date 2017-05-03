package org.aas.websocket.actor

import org.aas.websocket.model.{Table, TableWithoutId}
import org.scalatest.{FunSuite, Matchers}

import scalaz.{-\/, \/-}

class ListTableBufferTest extends FunSuite with Matchers {

  test("testInsert") {
    val list = new ListTableBuffer
    val tableWithoutId = TableWithoutId("some name", 12)
    val table00 = list.insert(-1, tableWithoutId)
    TableWithoutId(table00.name, table00.participants) should be(tableWithoutId)
    table00.id should be(0)

    val table01 = list.insert(-1, tableWithoutId)
    TableWithoutId(table01.name, table01.participants) should be(tableWithoutId)

    table01.id should be(1)
    list.all.head.id should be(table01.id)

    val table02 = list.insert(1, tableWithoutId)
    TableWithoutId(table02.name, table02.participants) should be(tableWithoutId)
    table02.id should be(2)

    list.all.head.id should be(table01.id)
    list.all.last.id should be(table00.id)
    list.all(1).id should be(table02.id)


    val table03 = list.insert(list.all.last.id, tableWithoutId)
    TableWithoutId(table03.name, table03.participants) should be(tableWithoutId)

    table03.id should be(3)
    list.all.last.id should be(table03.id)

    val table04 = list.insert(2000, tableWithoutId)
    TableWithoutId(table04.name, table04.participants) should be(tableWithoutId)

    table04.id should be(4)
    list.all.head.id should be(table04.id)
  }

  test("testRemoveTable") {
    val list = new ListTableBuffer
    val tableWithoutId00 = TableWithoutId("some name00", 11)
    val tableWithoutId01 = TableWithoutId("some name01", 10)

    list.insert(-1, tableWithoutId00)
    list.insert(-1, tableWithoutId01)

    list.removeTable(0).map(t => TableWithoutId(t.name, t.participants)) should be ( \/-(tableWithoutId00))
    assert( list.removeTable(234).isLeft )
    list.removeTable(1).map(t => TableWithoutId(t.name, t.participants)) should be ( \/-(tableWithoutId01))
  }

  test("testUpdate") {
    val list = new ListTableBuffer
    val tableWithoutId00 = TableWithoutId("some name00", 11)
    val tableWithoutId01 = TableWithoutId("some name01", 10)


    list.insert(-1, tableWithoutId00)
    list.insert(-1, tableWithoutId01)

    val table = Table(0, "new name", 777 )
    list.update(table) should be (\/-(table))
    assert( list.update(Table(345, "new name", 453)).isLeft )
  }

}
