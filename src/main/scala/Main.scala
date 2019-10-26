import akka.stream.Materializer
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.datastax.driver.core.{PlainTextAuthProvider, SocketOptions}
import com.example.groups.domain.Model.Network
import com.example.groups.http.{GroupService, Router}
import com.example.groups.storage.dto.AppDatabase
import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoint, KeySpace}

import scala.io.StdIn

object Main {

  object ConnectorExample2 {

    val default: CassandraConnection = ContactPoint.local
      .withClusterBuilder(
        _.withSocketOptions(
          new SocketOptions()
            .setConnectTimeoutMillis(20000)
            .setReadTimeoutMillis(20000)
        ).withAuthProvider(
          new PlainTextAuthProvider("cassandra", "cassandra")
        )
      ).keySpace("social_network")
  }

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem("groupsActorSystem")
    implicit val executionContext = system.dispatcher
    val materializer: Materializer = Materializer(system)

    val appDatabase = new AppDatabase(ConnectorExample2.default)

    val route = Router(new GroupService(Network()),appDatabase).routes()

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

  }


}
