package org.aas.websocket.graph

import akka.NotUsed
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

class SubscriptionFlow(ref: ActorRef) {

  import akka.pattern.ask

  implicit val askTimeout = Timeout(30 seconds)


  def flow: Flow[Parcel, Parcel, NotUsed] = {
    Flow[Parcel].statefulMapConcat { () =>
      val subscription = new SubscriptionInfo
      m => (subscription -> m) :: Nil
    }.mapConcat {
      case (subscription, subscribeTableRequest@SubscribeTablesRequest()) =>
        subscription.isSubscribe = true
        subscribeTableRequest :: Nil

      case (subscription, UnsubscribeTablesRequest()) =>
        subscription.isSubscribe = false
        Nil

      case (SubscriptionInfo(false), TableAddedEvent(_, _)) =>
        Nil

      case (SubscriptionInfo(false), TableUpdatedEvent(_)) =>
        Nil

      case (SubscriptionInfo(false), TableRemovedEvent(_)) =>
        Nil

      case (subscription, other) =>
        other :: Nil
    }.mapAsync(5) {
      case subscribeTablesRequest: SubscribeTablesRequest =>
        (ref ? subscribeTablesRequest).map(_.asInstanceOf[Parcel])

      case m => Future(m)
    }
  }

}

object SubscriptionFlow {
  def apply(ref: ActorRef) = new SubscriptionFlow(ref)
}
