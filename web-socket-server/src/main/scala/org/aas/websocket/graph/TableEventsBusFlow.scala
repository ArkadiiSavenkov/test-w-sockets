package org.aas.websocket.graph

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink, Source}
import org.aas.websocket.model.Model

import scala.concurrent.duration._


object TableEventsBusFlow {

  private def getSinkSourceOfBus(implicit materializer: Materializer): (Sink[Model, NotUsed], Source[Model, NotUsed]) = {
    MergeHub.source[Model](perProducerBufferSize = 16)
      .toMat(BroadcastHub.sink(bufferSize = 256))(Keep.both)
      .run()
  }

  def busFlow(implicit materializer: Materializer): Flow[Model, Model, Any] = {
    val (sink, source ) = getSinkSourceOfBus(materializer)
    Flow.fromSinkAndSource(sink, source)
      .backpressureTimeout(10.seconds)
  }
}
