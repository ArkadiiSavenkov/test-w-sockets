package org.aas.websocket.graph

import akka.stream.scaladsl.Flow
import org.aas.websocket.model._
import org.aas.websocket.service.User

object AuthorizationFlow {
  private def isAdminCommand(model: Model): Boolean = {
    model.isInstanceOf[AddTableRequest] || model.isInstanceOf[RemoveTableRequest] || model.isInstanceOf[UpdateTableRequest]
  }

  def flow: Flow[(Option[User], Model), Model, Any] = {
    Flow[(Option[User], Model)].mapConcat {
      case (Some(user), model) => {
        if (isAdminCommand(model) && (user.userName != "admin")) NotAuthorizedResponse() :: Nil
        else model :: Nil
      }
      case (_, model) => model :: Nil
    }
  }
}
