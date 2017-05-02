package org.aas.websocket

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.Directives.{handleWebSocketMessages, path}
import akka.stream.Supervision.Decider
import akka.stream._
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge}
import org.aas.websocket.actor.MemoryDbActor
import org.aas.websocket.graph._
import org.aas.websocket.model.{Model, PingRequest, PongResponse}
import org.aas.websocket.service.{AuthenticationService, IAuthenticationService, User}

import scala.io.StdIn
import scala.concurrent.ExecutionContext.Implicits.global

object WebSocketServer extends App {
  implicit val actorSystem = ActorSystem("example")
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(actorSystem)
    .withSupervisionStrategy(Supervision.resumingDecider.asInstanceOf[Decider]))

  val memoryDbActor: ActorRef = actorSystem.actorOf(Props[MemoryDbActor], "MemoryDbActor")


  val users = List(User("admin", "admin", "admin"), User("user", "password", "user"))
  val authenticationServer: IAuthenticationService = new AuthenticationService(users)

  val myFlow: Flow[Message, Message, NotUsed] = Flow.fromGraph(GraphDSL.create() { implicit builder =>
    import akka.stream.scaladsl.GraphDSL.Implicits._

    val start: FlowShape[Message, String] = builder.add(ConnectorFlows.flowStart)
    val finish: FlowShape[String, Message] = builder.add(ConnectorFlows.flowFinish)

    def pingPongFlow: Flow[Model, Model, Any] = {
      Flow[Model].map {
        case ping: PingRequest => PongResponse(ping.seq)
        case x => x
      }
    }

    val broadcast: UniformFanOutShape[Model, Model] = builder.add(Broadcast[Model](2))
    val merge: UniformFanInShape[Model, Model] = builder.add(Merge[Model](2))



    start ~> ModelConvertorFlows.flowStringToModel ~> FilterFlows.entryFlow ~> pingPongFlow ~>
      AuthenticationFlow(authenticationServer).flow ~> AuthorizationFlow.flow ~>
      MemoryDbFlow(memoryDbActor).flow ~>
      broadcast ~> FilterFlows.filterEvents ~> TableEventsBusFlow.busFlow ~> merge ~>
      SubscriptionFlow(memoryDbActor).flow ~> ModelConvertorFlows.modelToString ~> finish

    broadcast ~> FilterFlows.filterNotEvents ~> merge

    FlowShape(start.in, finish.out)

  })

  val route = path("ws")(handleWebSocketMessages(myFlow))
  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"WebSocketServer started at http://localhost:8080/  \nIf you press ENTER the server should shut down (I hope)")
  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ ⇒ actorSystem.terminate())
}
