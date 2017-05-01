package org.aas.websocket.model

import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo, JsonTypeName}
import com.fasterxml.jackson.annotation.JsonTypeInfo.As

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "@type")
@JsonSubTypes(Array(
  //---------Requests---------------------------------------------------------------
  new Type(value = classOf[LoginRequest], name="login"),
  new Type(value = classOf[PingRequest], name = "ping"),
  new Type(value = classOf[SubscribeTablesRequest], name ="subscribe_tables"),
  new Type(value = classOf[UnsubscribeTablesRequest], name ="unsubscribe_tables"),
  new Type(value = classOf[AddTableRequest], name = "add_table"),
  new Type(value = classOf[UpdateTableRequest], name="update_table"),
  new Type(value = classOf[RemoveTableRequest], name="remove_table"),
  //---------Responses--------------------------------------------------------------
  new Type(value = classOf[LoginSuccessfulResponse], name="login_successful"),
  new Type(value = classOf[LoginFailedResponse], name="login_failed"),
  new Type(value = classOf[PongResponse], name = "pong"),
  new Type(value = classOf[NotAuthorizedResponse], name = "not_authorized"),
  new Type(value = classOf[TableListResponse], name = "table_list"),
  new Type(value = classOf[RemovalFailedResponse], name = "removal_failed"),
  new Type(value = classOf[UpdateFailedResponse], name = "update_failed"),
  //---------Events-----------------------------------------------------------------
  new Type(value = classOf[TableAddedEvent], name="table_added"),
  new Type(value = classOf[TableUpdatedEvent], name = "table_updated"),
  new Type(value = classOf[TableRemovedEvent], name="table_removed")
))
trait Model {}

case class TableWithoutId
(
  name: String,
  participants: Int
)

case class Table
(
  id: String,
  name: String,
  participants: Int
)

//---------Requests---------------------------------------------------------------

case class LoginRequest(userName: String, password: String) extends Model

case class PingRequest(seq: Int) extends Model

case class SubscribeTablesRequest() extends Model

case class UnsubscribeTablesRequest() extends Model

case class AddTableRequest
(
  afterId: Int,
  table: TableWithoutId
) extends Model

case class UpdateTableRequest(table: Table) extends Model

case class RemoveTableRequest(id: Int) extends Model

//---------Responses--------------------------------------------------------------
case class LoginSuccessfulResponse(userType: String) extends Model

case class LoginFailedResponse() extends Model

case class PongResponse(seq: Int) extends Model

case class NotAuthorizedResponse() extends Model

case class TableListResponse(tables: List[Table]) extends Model

case class RemovalFailedResponse(id: Int) extends Model

case class UpdateFailedResponse(id: Int) extends Model


//---------Events-----------------------------------------------------------------

case class TableAddedEvent
(
  afterId: Int,
  table: Table
) extends Model

case class TableUpdatedEvent(table: Table) extends Model

case class TableRemovedEvent(id: Int) extends Model






