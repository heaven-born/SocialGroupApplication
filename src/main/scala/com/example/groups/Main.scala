package com.example.groups

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.datastax.driver.core.SocketOptions
import com.example.groups.domain.Model.Network
import com.example.groups.http.{GroupService, Router}
import com.github.nosan.embedded.cassandra.EmbeddedCassandraFactory
import com.github.nosan.embedded.cassandra.api.Cassandra
import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoint, KeySpace}
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.duration._

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.io.StdIn

object Main {


  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem("groupsActorSystem")
    implicit val executionContext = system.dispatcher

    val runtime = new DefaultRuntime{}
    val cassandraFactory: EmbeddedCassandraFactory = new EmbeddedCassandraFactory
    val cassandra: Cassandra = cassandraFactory.create

    if (sys.props.getOrElse("run_cassandra", "true").toBoolean){
      cassandra.start()
    }

    {
      import com.outworkers.phantom.dsl._
      val ec: ExecutionContextExecutor = ExecutionContext.global
      env.create(env.defaultTimeout)(ec)
    }

    runtime.unsafeRunToFuture(shardScheduler.provide(env))

    val routes = Router(new GroupService(Network()),env,runtime).routes()

    val bindingFuture = Http().bindAndHandle(routes, httpServerHost, httpServerPort)

    println(s"Server online at http://$httpServerHost:$httpServerPort/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

  }

  val shardUpdatePeriod = 1.minute
  val cassandraLogin = "cassandra"
  val cassandraPassword = "cassandra"
  val httpServerHost = "localhost"
  val httpServerPort = 8080

  val defaultConnection: CassandraConnection = ContactPoint.local
    .withClusterBuilder(
      _.withSocketOptions(new SocketOptions())
        .withCredentials(cassandraLogin, cassandraPassword)
    ).keySpace("social_network")

  val env = new Env(defaultConnection) with Console.Live with Clock.Live with Blocking.Live



  //this scheduler updates local cache of shards from time to time.
  def shardScheduler = {

    val updater = ZIO.accessM[Env] {env =>
      env.shards.findAll().map{s =>
        env.shardMapUnsafe = s
        println("Shards updated. New values: "+s.map(k=>("Group: "+k._1)->("Count: "+k._2.size)))
      }
    }

    val spaced = Schedule.spaced(shardUpdatePeriod)
    updater.repeat(spaced).forever

  }


}
