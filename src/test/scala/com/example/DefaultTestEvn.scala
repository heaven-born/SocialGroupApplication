package com.example

import java.util.UUID

import com.datastax.driver.core.SocketOptions
import com.example.groups.Env
import com.example.groups.storage.{group_members, posts, users}
import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoint}
import com.outworkers.phantom.database.Database
import zio.{Ref, ZIO}
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console

object DefaultTestEvn {

  val defaultConnection: CassandraConnection = ContactPoint.local
    .withClusterBuilder(
      _.withSocketOptions(new SocketOptions())
        .withCredentials("cassandra", "cassandra")
    ).keySpace("social_network")


  val env: ZIO[Any, Nothing, Env] = for {
    q <- Ref.make(Map[Int,Set[UUID]]())
  } yield new  Env(defaultConnection) with Blocking.Live {
    override val shards = q
  }

}
