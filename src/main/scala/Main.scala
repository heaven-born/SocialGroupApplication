import akka.stream.Materializer
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.datastax.driver.core.{PlainTextAuthProvider, SocketOptions}
import com.example.groups.domain.Model.Network
import com.example.groups.http.{GroupService, Router}
import com.example.groups.storage.AppDatabase
import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoint, KeySpace}

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
    val materializer: Materializer = Materializer(system)

    val appDatabase = new AppDatabase(defaultConnection)

    val route = Router(new GroupService(Network()),appDatabase).routes()

    val bindingFuture = Http().bindAndHandle(route, httpServerHost, httpServerPort)

    println(s"Server online at http://$httpServerHost:$httpServerPort/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

  }


}
