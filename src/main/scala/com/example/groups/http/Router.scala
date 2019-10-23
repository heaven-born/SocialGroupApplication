package com.example.groups.http

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.example.groups.http.dto.{NewGroupMemberDto, NewPostDto, NewUserDto}

object Router {

  def routes() = {
      concat(
        get {
          concat (
            path("list-groups") {
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>list-groups</h1>"))
            },
            pathPrefix("group-feed"/ IntNumber/"lastPostId"/ LongNumber) { (groupId,lastPostId) =>
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>group-feed id $groupId</h1>"))
            },
            pathPrefix("all-groups-feed"/"lastPostId"/ LongNumber) { (lastPostId) =>
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>all-groups-feed</h1>"))
            }
          )
        },
        post {
          concat(
            path("register-member") {
              entity(as[NewGroupMemberDto]) { group =>
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>group $group</h1>"))
              }
            },
            path("post") {
              entity(as[NewPostDto]) { post =>
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>post = $post</h1>"))
              }
            },
            path("register-user") {
              entity(as[NewUserDto]) { user =>
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>user = $user</h1>"))
              }
            }
          )
        }
      )
  }
}
