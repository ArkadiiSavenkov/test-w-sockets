package org.aas.websocket.cli

import java.util.UUID

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.typesafe.scalalogging.LazyLogging
import org.aas.websocket.model._
import org.asynchttpclient.ws.{WebSocket, WebSocketTextListener, WebSocketUpgradeHandler}
import org.asynchttpclient.{AsyncHttpClient, DefaultAsyncHttpClient, DefaultAsyncHttpClientConfig}

import scala.io.StdIn
import scala.util.{Failure, Success, Try}

object TestCli extends App with LazyLogging {
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
    val splitInputList: Array[String] = inputLine.split(" ")
    Try {
      splitInputList(0) match {
        case "exit" => break = true

        case "help" => showHelp

        case "login-admin" =>
          val loginRequest = LoginRequest("admin", "admin")
          websocket.sendMessage(mapper.writeValueAsString(loginRequest))

        case "login-user" =>
          val loginRequest = LoginRequest("user", "password")
          websocket.sendMessage(mapper.writeValueAsString(loginRequest))

        case "login-illegal" =>
          val loginRequest = LoginRequest("user", "password_illegal")
          websocket.sendMessage(mapper.writeValueAsString(loginRequest))

        case "illegal-command" =>
          websocket.sendMessage("{some-illegal_expre")

        case "add-table" =>
          val table = TableWithoutId("name_" + UUID.randomUUID(), 3)
          websocket.sendMessage(mapper.writeValueAsString(AddTableRequest(-1, table)))

        case "update-table" =>
          val table = Table(splitInputList(1).toLong, "name_" + UUID.randomUUID(), 6)
          websocket.sendMessage(mapper.writeValueAsString(UpdateTableRequest(table)))

        case "remove-table" =>
          websocket.sendMessage(mapper.writeValueAsString(RemoveTableRequest(splitInputList(1).toLong)))

        case "subscribe" =>
          websocket.sendMessage(mapper.writeValueAsString(SubscribeTablesRequest()))

        case "unsubscribe" =>
          websocket.sendMessage(mapper.writeValueAsString(UnsubscribeTablesRequest()))

        case _ =>
          println("Unknown command")
      }
    } match {
      case Failure(e) =>
        println("err-----------------")
        logger.error("Error->", e)
      case Success(_) =>
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
         |  login-admin - login as admin
         |  login-user - login as ordinary user
         |  login-illegal - login with wrong password
         |  illegal-command - send corrupt object
         |  add-table - insert random table
         |  update-table id - update table with id
         |  remove-table id - remove table with id
         |  subscribe - subscribe to notifications about changes in tables
         |  unsubscribe - unsubscribe from notifications about changes in tables
         """.stripMargin)
  }
}
