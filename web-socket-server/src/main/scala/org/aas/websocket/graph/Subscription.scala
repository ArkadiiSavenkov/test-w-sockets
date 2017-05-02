package org.aas.websocket.graph

import akka.actor.ActorRef
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import org.aas.websocket.model._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class SubscriptionInfo {
  @volatile var isSubscribe: Boolean = false
}

object SubscriptionInfo {
  def unapply(arg: SubscriptionInfo): Option[Boolean] = Some(arg.isSubscribe)
}

class Subscription(ref: ActorRef) {

  import akka.pattern.ask

  implicit val askTimeout = Timeout(30 seconds)


  def flow = {
    Flow[Model].statefulMapConcat { () =>
      val subscription = new SubscriptionInfo
      m => (subscription -> m) :: Nil
    }.mapConcat {
      case (subscription, subscribeTableRequest@SubscribeTablesRequest()) =>
        println("1")
        subscription.isSubscribe = true
        subscribeTableRequest :: Nil
      case (subscription, UnsubscribeTablesRequest()) =>
        println("2")
        subscription.isSubscribe = false
        Nil
      case (SubscriptionInfo(false), TableAddedEvent(_, _)) =>
        println("3")
        Nil
      case (SubscriptionInfo(false), TableUpdatedEvent(_)) =>
        println("4")
        Nil
      case (SubscriptionInfo(false), TableRemovedEvent(_)) =>
        println("5")
        Nil
      case (subscription, other) =>
        println("6")
        other :: Nil
    }.mapAsync(5) { m =>
      m match {
        case subscribeTablesRequest: SubscribeTablesRequest => {
          println("7")
          (ref ? subscribeTablesRequest).map(_.asInstanceOf[Model])
        }
        case _ =>
          println("8")
          Future(m)
      }
    }.map { m =>
      println(m)
      m
    }
  }

}

object Subscription {
  def apply(ref: ActorRef) = new Subscription(ref)
}
