package org.aas.websocket.cli

import java.util.{Timer, TimerTask, UUID}

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
          if (!(mapper.readValue(message, classOf[Model])).isInstanceOf[PongResponse])
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

  private def sendMessage(model: Model) = {
    websocket.sendMessage(mapper.writeValueAsString(model))
  }

  //temporary solution - i don't know (now) how hold the ws connection
  val timer = new Timer()
  timer.schedule(new TimerTask {
    override def run() = {
      sendMessage(PingRequest(1))
    }
  }, 30000, 30000)

  showHelp

  var break = false
  while (!break) {
    val inputLine = StdIn.readLine()
    if (inputLine != null) {
      val splitInputList: Array[String] = inputLine.split(" ")
      Try {
        splitInputList(0) match {
          case "exit" => break = true

          case "help" => showHelp

          case "login-admin" => sendMessage(LoginRequest("admin", "admin"))

          case "login-user" => sendMessage(LoginRequest("user", "password"))

          case "login-illegal" => sendMessage(LoginRequest("user", "password_illegal"))

          case "illegal-command" => websocket.sendMessage("{some-illegal_expre")

          case "add-table" => sendMessage(AddTableRequest(-1, TableWithoutId("name_" + UUID.randomUUID(), 3)))

          case "update-table" =>
            val table = Table(splitInputList(1).toLong, "name_" + UUID.randomUUID(), 6)
            sendMessage(UpdateTableRequest(table))

          case "remove-table" =>
            sendMessage(RemoveTableRequest(splitInputList(1).toLong))

          case "subscribe" =>
            sendMessage(SubscribeTablesRequest())

          case "unsubscribe" =>
            sendMessage(UnsubscribeTablesRequest())

          case _ =>
            println("Unknown command")
        }
      } match {
        case Failure(e) =>
          logger.error("Error->", e)
        case Success(_) =>
      }
    }
  }

  timer.cancel()
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
         |  subscribe - subscribe to changes in tables notifications
         |  unsubscribe - unsubscribe from changes in tables notifications
         """.stripMargin)
  }
}
