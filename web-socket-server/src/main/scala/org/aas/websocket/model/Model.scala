package org.aas.websocket.model

trait Model {}

case class LoginRequest(userName: String, password: String) extends Model

case class LoginSuccessfulResponse(userType: String) extends Model

case class LoginFailedResponse() extends Model

case class PingRequest(seq: Int) extends Model

case class PongResponse(seq: Int) extends Model

case class Table
(
  id: String,
  name: String,
  participants: Int
)

case class TableWithId
(
  id: String,
  name: String,
  participants: Int
)

case class TableListResponse(tables: List[TableWithId]) extends Model

case class SubscribeTablesRequest() extends Model

case class SubscribeTablesResponse() extends Model

case class UnsubscribeTablesRequest() extends Model

case class NotAuthorizedResponse() extends Model

case class AddTableRequest
(
  afterId: Int,
  table: Table
) extends Model

case class UpdateTableRequest(table: TableWithId) extends Model

case class RemoveTableRequest(id: Int) extends Model




