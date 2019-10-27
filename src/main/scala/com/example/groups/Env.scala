package com.example.groups

import java.util.UUID

import com.example.groups.storage.{group_members, posts, shards, users}
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.database.Database
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console


abstract class Env(override val connector: CassandraConnection)
      extends Database[Env](connector) with Console.Live with Clock.Live with Blocking.Live {
  object users extends users with Connector
  object groups extends group_members with Connector
  object posts extends posts with Connector
  object shards extends shards with Connector

  val defaultShard = UUID.fromString("620d359c-ce4d-4d4a-8587-942c69dd57da")


  // this is really not very good solution. The reason why I did it, is that I could not share
  // this field as Ref across the entire application because akka http doesn't support ZIO natively.
  // so there is multiple places where I had to run runtime.unsafeRun(...)
  // I also didn't provide "volatile" here because it's not critical if changes will not be visible for some
  // thread for some period of time.
  var shardMapUnsafe:Map[Int,Seq[UUID]] = Map()



}


