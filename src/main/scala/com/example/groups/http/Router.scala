package com.example.groups.http

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._

object Router {

  def routes() = {
      concat(
        get {
          concat (
            path("list-groups") {
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>list-groups</h1>"))
            },
            pathPrefix("group-feed"/ IntNumber) { groupId =>
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>group-feed id $groupId</h1>"))
            },
            path("all-groups-feed") {
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>all-groups-feed</h1>"))
            },
            pathPrefix("become-member"/ "user" / IntNumber / "group" / IntNumber) { (userId, groupId) =>
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>group-feed id $groupId</h1>"))
            },
          )
        },
        path("post"/"group"/IntNumber/"user"/IntNumber) { (groupId, userId) =>
          post {
            entity(as[String]) { message =>
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>group = $groupId message = $message</h1>"))
            }
          }
        }
      )
  }
}
