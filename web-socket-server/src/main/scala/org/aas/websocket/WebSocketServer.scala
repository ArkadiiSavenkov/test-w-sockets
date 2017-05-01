package org.aas.websocket

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.Directives.{handleWebSocketMessages, path}
import akka.stream.{ActorMaterializer, FlowShape}
import akka.stream.scaladsl.{Flow, GraphDSL}
import org.aas.websocket.graph.{Authentication, Connectors, ModelConvertors}
import org.aas.websocket.model.Model
import org.aas.websocket.service.{AuthenticationService, IAuthenticationService, User}

import scala.io.StdIn
import scala.concurrent.ExecutionContext.Implicits.global

object WebSocketServer extends App {
  implicit val system = ActorSystem("example")
  implicit val materializer = ActorMaterializer()

  val users = List(User("admin", "admin", "admin"), User("user", "password", "user"))
  val authenticationServer: IAuthenticationService = new AuthenticationService(users)

  val myFlow: Flow[Message, Message, NotUsed] = Flow.fromGraph(GraphDSL.create() { implicit builder =>
    import akka.stream.scaladsl.GraphDSL.Implicits._

    val start: FlowShape[Message, String] = builder.add(Connectors.flowStart)
    val finish: FlowShape[String, Message] = builder.add(Connectors.flowFinish)

    def tempFlow = {
      Flow[(Option[User], Model)].map(s => s._2)
    }

    start ~> ModelConvertors.flowStringToModel ~> Authentication(authenticationServer).flow ~> tempFlow ~> ModelConvertors.modelToString ~> finish

    FlowShape(start.in, finish.out)

  })

  val route = path("ws")(handleWebSocketMessages(myFlow))
  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"WebSocketServer started at http://localhost:8080/  \nFor quit server please press ENTER")
  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ ⇒ system.terminate())
}
