package org.aas.websocket.graph

import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl.Flow

object Connectors {

  def flowStart: Flow[Message, String, Any] = {
    Flow[Message].collect {
      case TextMessage.Strict(t) ⇒ t
    }
  }

  def flowFinish: Flow[String, Message, Any] = {
    Flow[String].map(text ⇒
      TextMessage.Strict(text))
  }
}
