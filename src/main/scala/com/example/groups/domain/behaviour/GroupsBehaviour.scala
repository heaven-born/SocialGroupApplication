package com.example.groups.domain.behaviour

import java.util.UUID

import com.example.groups.domain.Model._
import zio.{Task, ZIO}

trait GroupsBehaviour {
  self: Groups =>

    def get(groupId: Int): Task[Group] = ???
    def get(groupId: Int, userId: UUID): Task[Group] = ZIO.effectTotal(Group(UUID.randomUUID()))
    def byUser(userId: UUID): Task[GroupSet] = ???

}
