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

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object DefaultTestEvn {

  val defaultConnection: CassandraConnection = ContactPoint.local
    .withClusterBuilder(
      _.withSocketOptions(new SocketOptions())
        .withCredentials("cassandra", "cassandra")
    ).keySpace("social_network_test")


  val env = new  Env(defaultConnection) with Blocking.Live


}
