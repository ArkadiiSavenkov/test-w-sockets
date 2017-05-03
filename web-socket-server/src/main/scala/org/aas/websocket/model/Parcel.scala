package org.aas.websocket.model

import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo, JsonTypeName}
import com.fasterxml.jackson.annotation.JsonTypeInfo.As

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "$type")
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
  new Type(value = classOf[NotAuthenticateResponse], name = "not_authenticate"),
  new Type(value = classOf[TableListResponse], name = "table_list"),
  new Type(value = classOf[RemovalFailedResponse], name = "removal_failed"),
  new Type(value = classOf[UpdateFailedResponse], name = "update_failed"),
  //---------Events-----------------------------------------------------------------
  new Type(value = classOf[TableAddedEvent], name="table_added"),
  new Type(value = classOf[TableUpdatedEvent], name = "table_updated"),
  new Type(value = classOf[TableRemovedEvent], name="table_removed")
))
trait Parcel {}

case class TableWithoutId
(
  name: String,
  participants: Int
)

case class Table
(
  id: Long,
  name: String,
  participants: Int
)

//---------Requests---------------------------------------------------------------

case class LoginRequest(userName: String, password: String) extends Parcel

case class PingRequest(seq: Int) extends Parcel

case class SubscribeTablesRequest() extends Parcel

case class UnsubscribeTablesRequest() extends Parcel

case class AddTableRequest
(
  afterId: Long,
  table: TableWithoutId
) extends Parcel

case class UpdateTableRequest(table: Table) extends Parcel

case class RemoveTableRequest(id: Long) extends Parcel

//---------Responses--------------------------------------------------------------
case class LoginSuccessfulResponse(userType: String) extends Parcel

case class LoginFailedResponse() extends Parcel

case class PongResponse(seq: Int) extends Parcel

case class NotAuthorizedResponse() extends Parcel

case class NotAuthenticateResponse() extends Parcel

case class TableListResponse(tables: List[Table]) extends Parcel

case class RemovalFailedResponse(id: Long) extends Parcel

case class UpdateFailedResponse(id: Long) extends Parcel


//---------Events-----------------------------------------------------------------

case class TableAddedEvent
(
  afterId: Long,
  table: Table
) extends Parcel

case class TableUpdatedEvent(table: Table) extends Parcel

case class TableRemovedEvent(id: Long) extends Parcel






