package org.aas.websocket.model

import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.scalatest.{Assertion, FunSuite, Matchers}

class ModelTest extends FunSuite with Matchers {
  val mapper: ObjectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
  mapper.registerModule(DefaultScalaModule)

  def compareAsJson(value1: String, value2: String): Assertion = {
    mapper.readValue(value1, classOf[Map[String, Any]]) should be(mapper.readValue(value2, classOf[Map[String, Any]]))
  }

  def compareAsJson(value1: String, value2: Map[String, Any]): Assertion = {
    mapper.readValue(value1, classOf[Map[String, Any]]) should be(value2)
  }

  def compareAsJson(obj: AnyRef, value2: Map[String, Any]): Assertion = {
    mapper.readValue(mapper.writeValueAsString(obj), classOf[Map[String, Any]]) should be(value2)
  }

  test("Model classes are serialized correctly") {
    compareAsJson(LoginRequest("UserName", "Password"),
      Map("@type" -> "login", "userName" -> "UserName", "password" -> "Password"))

    compareAsJson(LoginSuccessfulResponse("admin"),
      Map("@type" -> "login_successful", "userType" -> "admin"))

    compareAsJson(mapper.writeValueAsString(LoginSuccessfulResponse("admin")),
      Map("@type" -> "login_successful",
        "userType" -> "admin"))

    compareAsJson(AddTableRequest(54, TableWithoutId("some table name", 345)),
      Map("@type" -> "add_table", "afterId" -> 54,
        "table" -> Map("name" -> "some table name", "participants" -> 345)))
  }


}
