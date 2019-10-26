package com.example.groups.domain.behaviour

import java.util.UUID

import com.example.groups.domain.Model._
import com.example.groups.storage.AppDatabase
import zio.{RIO, Task, ZIO}

trait GroupsBehaviour {
  self: Groups =>

    def get(groupId: Int): Task[Group] = ZIO.effectTotal(Group(groupId)) // for simplicity assume that all groups already exist
    def get(groupId: Int, memberUserId: UUID): Task[Group] = ZIO.effectTotal(Group(0))
    def byUser(userId: UUID): RIO[AppDatabase,GroupSet] =
      ZIO.accessM[AppDatabase] { _.groups.findByUserId(userId) }
        .map(l => GroupSet(l.map(_.id).toSet))

}
