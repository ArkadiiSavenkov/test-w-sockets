package org.aas.websocket.graph

import akka.stream.scaladsl.Flow
import org.aas.websocket.model._
import org.aas.websocket.service.User

object AuthorizationFlow {
  private def isAdminCommand(parcel: Parcel): Boolean = {
    parcel.isInstanceOf[AddTableRequest] || parcel.isInstanceOf[RemoveTableRequest] || parcel.isInstanceOf[UpdateTableRequest]
  }

  def flow: Flow[(Option[User], Parcel), Parcel, Any] = {
    Flow[(Option[User], Parcel)].mapConcat {
      case (Some(user), parcel) =>
        if (isAdminCommand(parcel) && (user.userName != "admin")) NotAuthorizedResponse() :: Nil
        else parcel :: Nil

      case (_, parcel) => parcel :: Nil
    }
  }
}
