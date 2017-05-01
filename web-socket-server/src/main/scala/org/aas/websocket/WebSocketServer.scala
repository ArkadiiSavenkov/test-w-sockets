package org.aas.websocket

import org.aas.websocket.service.{AuthenticationService, IAuthenticationService, User}

object WebSocketServer extends App {
  val users = List(User("admin", "admin", "admin"), User("user", "user", "user"))
  val authenticationServer : IAuthenticationService = new AuthenticationService(users)


}
