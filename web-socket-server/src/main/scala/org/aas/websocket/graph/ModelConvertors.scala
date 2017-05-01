package org.aas.websocket.graph

import akka.stream.scaladsl.Flow
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.aas.websocket.model.Model

object ModelConvertors {
  val mapper = new ObjectMapper
  mapper.registerModule(DefaultScalaModule)

  def flowStringToModel: Flow[String, Model, Any] = {
    Flow[String].map(s => mapper.readValue(s, classOf[Model]))
  }

  def modelToString: Flow[Model, String, Any] = {
    Flow[Model].map(m => mapper.writeValueAsString(m))
  }

}
