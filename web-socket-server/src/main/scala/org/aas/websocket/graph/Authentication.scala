package org.aas.websocket.graph

import akka.NotUsed
import akka.stream.scaladsl.Flow
import org.aas.websocket.model.{LoginFailedResponse, LoginRequest, LoginSuccessfulResponse, Model}
import org.aas.websocket.service.{IAuthenticationService, User}

import scalaz.{-\/, \/-}

private class AuthenticationInfo
(
  @volatile var user: Option[User] = None
)

private object AuthenticationInfo {
  def unapply(arg: AuthenticationInfo): Option[User] = arg.user
}

class Authentication(authenticationService: IAuthenticationService) {
  def flow: Flow[Model, (Option[User], Model), NotUsed] = {
    Flow[Model].statefulMapConcat { () =>
      val authInfo = new AuthenticationInfo
      m => (authInfo -> m) :: Nil
    }.mapConcat {
      case (authentication, LoginRequest(userName, password)) =>
        authenticationService.authenticate(userName, password) match {
          case \/-(user) =>
            authentication.user = Some(user)
            (Some(user), LoginSuccessfulResponse(user.userType)) :: Nil
          case -\/(error) =>
            authentication.user = None
            (None, LoginFailedResponse()) :: Nil
        }
      case (AuthenticationInfo(user), model) =>
        (Some(user), model) :: Nil

      case _ => {
        Nil
      }
    }
  }
}

object Authentication {
  def apply(authenticationService: IAuthenticationService) = new Authentication(authenticationService)
}


