package org.aas.websocket.graph

import akka.NotUsed
import akka.stream.scaladsl.Flow
import org.aas.websocket.model._
import org.aas.websocket.service.{IAuthenticationService, User}

import scalaz.{-\/, \/-}

private class AuthenticationInfo
(
  @volatile var user: Option[User] = None
)

private object AuthenticationInfo {
  def unapply(arg: AuthenticationInfo): Option[User] = arg.user
}

class AuthenticationFlow(authenticationService: IAuthenticationService) {
  def flow: Flow[Parcel, (Option[User], Parcel), NotUsed] = {
    Flow[Parcel].statefulMapConcat { () =>
      val authInfo = new AuthenticationInfo
      m => (authInfo -> m) :: Nil
    }.mapConcat {
      case (authentication, LoginRequest(userName, password)) =>
        authenticationService.authenticate(userName, password) match {
          case \/-(user) =>
            authentication.user = Some(user)
            if (user.userType == "admin")
              (Some(user), LoginSuccessfulResponse(user.userType)) :: (Some(user), SubscribeTablesRequest()) :: Nil
            else
              (Some(user), UnsubscribeTablesRequest()) :: (Some(user), LoginSuccessfulResponse(user.userType)) :: Nil
          case -\/(_) =>
            authentication.user = None
            (None, LoginFailedResponse()) :: Nil
        }
      case (AuthenticationInfo(user), parcel) =>
        (Some(user), parcel) :: Nil

      case (_, p@PongResponse(_)) => (None, p) :: Nil
      case _ => (None, NotAuthenticateResponse()) :: Nil
    }
  }
}

object AuthenticationFlow {
  def apply(authenticationService: IAuthenticationService) = new AuthenticationFlow(authenticationService)
}


