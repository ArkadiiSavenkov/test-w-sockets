package org.aas.websocket.graph

import akka.stream.scaladsl.Flow
import org.aas.websocket.model.{Parcel, PingRequest, PongResponse}

object PingPongFlow {
  def flow: Flow[Parcel, Parcel, Any] = {
    Flow[Parcel].map {
      case ping: PingRequest => PongResponse(ping.seq)
      case x => x
    }
  }
}
