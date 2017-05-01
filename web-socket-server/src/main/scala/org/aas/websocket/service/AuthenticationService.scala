package org.aas.websocket.service

import scalaz.{-\/, \/, \/-}

case class User
(
  userName: String,
  password: String,
  userType: String
)

trait IAuthenticationService {
  def authenticate(userName: String, password: String): String \/ User
}

class AuthenticationService(users: List[User]) extends IAuthenticationService {

  override def authenticate(userName: String, password: String): String \/ User = {
    users.find(u => (u.userName == userName && u.password == password))
      .map(\/-(_))
      .getOrElse(-\/("User doesn't exist"))
  }
}
