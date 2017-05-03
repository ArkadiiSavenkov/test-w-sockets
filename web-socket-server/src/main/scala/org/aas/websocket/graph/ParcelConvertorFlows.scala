package org.aas.websocket.graph

import akka.stream.scaladsl.Flow
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.aas.websocket.model.Parcel

object ParcelConvertorFlows {
  val mapper = new ObjectMapper
  mapper.registerModule(DefaultScalaModule)

  def flowStringToParcel: Flow[String, Parcel, Any] = {
    Flow[String].map(s => mapper.readValue(s, classOf[Parcel]))
  }

  def parcelToString: Flow[Parcel, String, Any] = {
    Flow[Parcel].map(p => mapper.writeValueAsString(p))
  }

}
