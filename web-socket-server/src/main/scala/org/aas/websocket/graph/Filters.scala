package org.aas.websocket.graph

import akka.stream.scaladsl.Flow
import org.aas.websocket.model._

class Filters {
  def entryFilter: Flow[Model, Model, Any] = {
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
}
