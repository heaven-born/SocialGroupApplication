package com.example.groups.domain.behaviour

import java.util.UUID

import com.example.groups.domain.Model._
import zio.{Task, ZIO}

trait GroupsBehaviour {
  self: Groups =>

    def list(): Task[Set[Group]] = ???
    def get(groupId: UUID): Task[Group] = ???
    def get(groupId: UUID, userId: UUID): Task[Group] = ZIO.effectTotal(Group(UUID.randomUUID()))
    def byUser(userId: UUID): Task[GroupSet] = ???

}
