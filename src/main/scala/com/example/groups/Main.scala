package com.example.groups

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.datastax.driver.core.SocketOptions
import com.example.groups.domain.Model.Network
import com.example.groups.http.{GroupService, Router}
import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoint}
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.duration._

import scala.io.StdIn

object Main {

  val cassandraLogin = "cassandra"
  val cassandraPassword = "cassandra"
  val httpServerHost = "localhost"
  val httpServerPort = 8080

  val defaultConnection: CassandraConnection = ContactPoint.local
    .withClusterBuilder(
      _.withSocketOptions(new SocketOptions())
       .withCredentials(cassandraLogin, cassandraPassword)
    ).keySpace("social_network")

  val env: ZIO[Any, Nothing, Env] = for {
    q <- Ref.make(Map[Int,Set[UUID]]())
  } yield new Env(defaultConnection) with Console.Live with Clock.Live with Blocking.Live {
    override val shards = q
  }

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem("groupsActorSystem")
    implicit val executionContext = system.dispatcher

    val routes = Router(new GroupService(Network()),env).routes()

    val bindingFuture = Http().bindAndHandle(routes, httpServerHost, httpServerPort)

    println(s"Server online at http://$httpServerHost:$httpServerPort/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

  }

  def buildRoutes = {

    val spaced = Schedule.spaced(1.seconds)
    val res = ZIO.effect(println("bla")).repeat(spaced).forever
    val runtime = new DefaultRuntime{}
    runtime.unsafeRunToFuture(res)

  }


}
