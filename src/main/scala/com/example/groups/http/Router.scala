package com.example.groups.http

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.example.groups.http.dto.{RegisterMemberDto, PostDto, UserDto}

object Router {

  def routes() = {
      concat(
        get {
          concat (
            path("list-groups") {
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>list-groups</h1>"))
            },
            path("group-feed") {
              parameters("start-from-post-id".?, "groupId".as[Long], "number-posts-to-load".as[Int]) {
               (lastPostId, groupId, numberPosts) =>
                    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>group-feed id $groupId</h1>"))
              }
            },
            path("all-groups-feed") {
              parameters("start-from-post-id".?, "groupId".as[Long], "number-posts-to-load".as[Int]) {
                (lastPostId, groupId, numberPosts) =>
                  complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>group-feed id $groupId</h1>"))
              }
            }
          )
        },
        post {
          concat(
            path("register-group-member") {
              entity(as[RegisterMemberDto]) { group =>
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>group $group</h1>"))
              }
            },
            path("post") {
              entity(as[PostDto]) { post =>
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>post = $post</h1>"))
              }
            },
            path("register-user") {
              entity(as[UserDto]) { user =>
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>user = $user</h1>"))
              }
            }
          )
        }
      )
  }
}
