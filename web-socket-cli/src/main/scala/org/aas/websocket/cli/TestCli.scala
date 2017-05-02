package org.aas.websocket.cli

import java.util.UUID

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.aas.websocket.model.{AddTableRequest, LoginRequest, TableWithoutId}
import org.asynchttpclient.ws.{WebSocket, WebSocketTextListener, WebSocketUpgradeHandler}
import org.asynchttpclient.{AsyncHttpClient, DefaultAsyncHttpClient, DefaultAsyncHttpClientConfig}

import scala.io.StdIn

object TestCli extends App {
  val mapper = new ObjectMapper
  mapper.registerModule(DefaultScalaModule)

  val cf: DefaultAsyncHttpClientConfig = new DefaultAsyncHttpClientConfig.Builder().build()
  val asyncHttpClient: AsyncHttpClient = new DefaultAsyncHttpClient(cf)

  val websocket = asyncHttpClient.prepareGet("ws://127.0.0.1:8080/ws")
    .execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(
      new WebSocketTextListener() {
        override def onMessage(message: String): Unit = {
          println(message)
        }

        override def onError(t: Throwable): Unit = {
          println("error ->" + t)
        }

        override def onClose(websocket: WebSocket): Unit = {
          println("close")
        }

        override def onOpen(websocket: WebSocket): Unit = {
        }
      }).build()).get();

  showHelp

  var break = false
  while (!break) {
    val inputLine = StdIn.readLine()
    inputLine match {
      case "exit" => break = true

      case "help" => showHelp

      case "login_admin" =>
        val loginRequest = LoginRequest("admin", "admin")
        websocket.sendMessage(mapper.writeValueAsString(loginRequest))

      case "login_user" =>
        val loginRequest = LoginRequest("user", "password")
        websocket.sendMessage(mapper.writeValueAsString(loginRequest))

      case "login_illegal" =>
        val loginRequest = LoginRequest("user", "password_illegal")
        websocket.sendMessage(mapper.writeValueAsString(loginRequest))

      case "illegal_command" =>
        websocket.sendMessage("{some-illegal_expre")

      case "insert_table" =>
        val table = TableWithoutId("name_" + UUID.randomUUID(), 3)
        websocket.sendMessage(mapper.writeValueAsString(AddTableRequest(-1, table)))

      case _ =>
        println("Unknown command")
    }
  }

  websocket.close()
  asyncHttpClient.close()

  def showHelp = {
    println(
      s"""
         |Use the next commands
         |  help - help
         |  exit - exit the program
         |  login_admin - login as admin
         |  login_user - login as ordinary user
         |  login_illegal - login with wrong password
         |  illegal_command - send corrupt object
         |  insert_table - insert random table
       """.stripMargin)
  }
}
