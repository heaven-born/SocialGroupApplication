package com.example.groups

import java.util.UUID

import com.example.groups.storage.{group_members, posts, users}
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.database.Database
import zio.Ref
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console

abstract class Env(override val connector: CassandraConnection)
      extends Database[Env](connector) with Console.Live with Clock.Live with Blocking.Live {
  object users extends users with Connector
  object groups extends group_members with Connector
  object posts extends posts with Connector
  val shards:Ref[Map[Int,Set[UUID]]]
  val defaultShard = UUID.fromString("620d359c-ce4d-4d4a-8587-942c69dd57da")
}

