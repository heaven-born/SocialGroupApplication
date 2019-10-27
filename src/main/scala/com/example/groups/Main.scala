package com.example.groups

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import com.datastax.driver.core.SocketOptions
import com.example.groups.domain.Model.Network
import com.example.groups.http.{GroupService, Router}
import com.example.groups.storage.{group_members, users}
import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoint}
import com.outworkers.phantom.database.Database
import zio.{Ref, ZIO}
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console

import scala.io.StdIn

object Main {

  abstract class AppDatabase(override val connector: CassandraConnection)
      extends Database[AppDatabase](connector) with Console.Live with Clock.Live with Blocking.Live{
    object users extends users with Connector
    object groups extends group_members with Connector
    val shards:Ref[Map[UUID,Set[String]]]
  }

  val cassandraLogin = "cassandra"
  val cassandraPassword = "cassandra"
  val httpServerHost = "localhost"
  val httpServerPort = 8080

  val defaultConnection: CassandraConnection = ContactPoint.local
    .withClusterBuilder(
      _.withSocketOptions(new SocketOptions())
       .withCredentials("cassandra", "cassandra")
    ).keySpace("social_network")

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem("groupsActorSystem")
    implicit val executionContext = system.dispatcher

    val bindingFuture = Http().bindAndHandle(buildRoutes, httpServerHost, httpServerPort)

    println(s"Server online at http://$httpServerHost:$httpServerPort/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

  }

  def buildRoutes = {


    val env: ZIO[Any, Nothing, AppDatabase] = for {
      q <- Ref.make(Map[UUID,Set[String]]())
    } yield new  AppDatabase(defaultConnection) with Console.Live with Clock.Live with Blocking.Live {
      override val shards = q
    }

    //val appDatabase = new AppDatabase(defaultConnection)
    Router(new GroupService(Network()),env).routes()
  }


}
