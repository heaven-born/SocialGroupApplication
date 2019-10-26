package com.example.groups.storage.dto

import java.util.UUID

import com.example.groups.domain.Model.{Group, User}
import com.example.groups.storage.dto.Model.{group_member, user}
import com.outworkers.phantom.dsl._
import zio.{Task, ZIO}

object Model {

  case class group_member(user_id:UUID,group_id: Int) //extends Udt
  case class user(user_id: UUID, user_name: String) //extends Udt
}

abstract class users extends Table[users, user] {
  object user_id extends UUIDColumn with PartitionKey
  object user_name extends StringColumn

  def findById(id: UUID) = ZIO.fromFuture{ _ =>
    select.where(_.user_id eqs id).one().map(_.get)
  }

  def exists(userId: UUID): Task[Boolean] = findById(userId).fold(_=>false, _=>true)

  def register(user: User): Task[User] = ZIO.fromFuture { _ =>
    insert
      .value(_.user_id, user.id)
      .value(_.user_name, user.name).future().map(_=>user)
  }
}

abstract class group_members extends Table[group_members, group_member] {
  object user_id extends UUIDColumn with PartitionKey
  object group_id extends IntColumn

  def register(groupId: Int, userId: UUID): Task[Group] = ZIO.fromFuture { _ =>
    insert
      .value(_.user_id, userId)
      .value(_.group_id, groupId)
      .future()
      .map(_=>Group(groupId))
  }
}

class AppDatabase(
                   override val connector: CassandraConnection
                 ) extends Database[AppDatabase](connector) {
  object users extends users with Connector
  object groups extends group_members with Connector
}

