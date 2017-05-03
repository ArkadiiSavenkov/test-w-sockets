package org.aas.websocket.graph

import akka.NotUsed
import akka.stream.scaladsl.Flow
import org.aas.websocket.model._

object FilterFlows {
  def entryFlow: Flow[Parcel, Parcel, Any] = {
    Flow[Parcel].filter { parcel =>
      parcel.isInstanceOf[LoginRequest] ||
        parcel.isInstanceOf[PingRequest] ||
        parcel.isInstanceOf[SubscribeTablesRequest] ||
        parcel.isInstanceOf[UnsubscribeTablesRequest] ||
        parcel.isInstanceOf[AddTableRequest] ||
        parcel.isInstanceOf[UpdateTableRequest] ||
        parcel.isInstanceOf[RemoveTableRequest]
    }
  }

  private def isParcelEvent(parcel : Parcel) = {
    parcel.isInstanceOf[TableAddedEvent] || parcel.isInstanceOf[TableRemovedEvent] || parcel.isInstanceOf[TableUpdatedEvent]
  }

  def filterEvents: Flow[Parcel, Parcel, NotUsed] = {
    Flow[Parcel].filter(m => isParcelEvent(m))
  }

  def filterNotEvents: Flow[Parcel, Parcel, NotUsed] = {
    Flow[Parcel].filterNot(m => isParcelEvent(m))
  }
}
