package com.example.groups.domain.behaviour

import java.util.UUID

import com.example.groups.Main.Env
import com.example.groups.domain.Model._
import zio.{RIO, Task, ZIO}

trait GroupsBehaviour {
  self: Groups =>

    def get(groupId: Int): Task[Group] = ZIO.effectTotal(Group(groupId)) // for simplicity assume that all groups already exist

    def get(groupId: Int, memberUserId: UUID): RIO[Env,Group] = {
      for {
        groups <- ZIO.accessM[Env] {_.groups.findByUserId (memberUserId)}
        res <- if (!groups.map(_.id).toSet.contains(groupId))
                 ZIO.fail(new IllegalStateException(s"User $memberUserId is not a member of group $groupId"))
               else
                  ZIO.effectTotal(Group(groupId))

      } yield res

    }

    def byUser(userId: UUID): RIO[Env,GroupSet] =
      ZIO.accessM[Env] { _.groups.findByUserId(userId) }
        .map(l => GroupSet(l.map(_.id).toSet))

}
