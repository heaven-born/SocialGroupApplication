package com.example.groups

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import com.datastax.driver.core.SocketOptions
import com.example.groups.domain.Model.Network
import com.example.groups.http.{GroupService, Router}
import com.example.groups.storage.AppDatabase
import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoint}

import scala.io.StdIn

object Main {

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
    val appDatabase = new AppDatabase(defaultConnection)
    Router(new GroupService(Network()),appDatabase).routes()
  }


}
