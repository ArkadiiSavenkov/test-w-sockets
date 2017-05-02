package org.aas.websocket.graph

import akka.stream.scaladsl.Flow
import org.aas.websocket.model._

object FilterFlows {
  def entryFlow: Flow[Model, Model, Any] = {
    Flow[Model].filter { model =>
      model.isInstanceOf[LoginRequest] ||
        model.isInstanceOf[PingRequest] ||
        model.isInstanceOf[SubscribeTablesRequest] ||
        model.isInstanceOf[UnsubscribeTablesRequest] ||
        model.isInstanceOf[AddTableRequest] ||
        model.isInstanceOf[UpdateTableRequest] ||
        model.isInstanceOf[RemoveTableRequest]
    }
  }

  private def isModelEvent(model : Model) = {
    model.isInstanceOf[TableAddedEvent] || model.isInstanceOf[TableRemovedEvent] || model.isInstanceOf[TableUpdatedEvent]
  }

  def filterEvents = {
    Flow[Model].filter(m => isModelEvent(m))
  }

  def filterNotEvents = {
    Flow[Model].filterNot(m => isModelEvent(m))
  }
}
