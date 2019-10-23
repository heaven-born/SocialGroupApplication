import akka.stream.Materializer
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.example.groups.domain.Model.Network
import com.example.groups.http.{GroupService, Router}

import scala.io.StdIn

object Main {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem("groupsActorSystem")
    implicit val executionContext = system.dispatcher
    val materializer: Materializer = Materializer(system)

    val route = Router.routes(new GroupService(Network()))

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

  }

}
