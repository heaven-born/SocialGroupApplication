package com.example.groups.domain.behaviour

import java.util.UUID

import com.example.groups.domain.Model._
import zio.{Task, ZIO}

trait GroupsBehaviour {
  self: Groups =>

    def get(groupId: Int): Task[Group] = ZIO.effectTotal(Group(groupId)) // for simplicity assume that all groups already exist
    def get(groupId: Int, userId: UUID): Task[Group] = ZIO.effectTotal(Group(0))
    def byUser(userId: UUID): Task[GroupSet] = ???

}
