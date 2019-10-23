package com.example.groups.http

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.example.groups.http.dto.{RegisterMemberDto, PostDto, UserDto}
import com.example.groups.http.dto.JsonSupport._

object Router {

  def routes(service: GroupService) = {
      concat(
        get {
          concat (
            path("list-groups") {
              complete(service.listGroups())
            },
            path("group-feed") {
              parameters("groupId".as[Long], "start-from-post-id".as[Long].?,  "number-posts-to-load".as[Int].?) {
               (groupId, lastPostId, numberPosts) =>
                    complete(service.groupFeed(groupId, lastPostId, numberPosts))
              }
            },
            path("all-groups-feed") {
              parameters("start-from-post-id".as[Long].?,  "number-posts-to-load".as[Int].?) {
                (lastPostId, numberPosts) =>
                  complete(service.allGroupsFeed(lastPostId, numberPosts))
              }
            }
          )
        },
        post {
          concat(
            path("register-group-member") {
              entity(as[RegisterMemberDto]) { group =>
                complete(service.registerGroupMember(group))
              }
            },
            path("post-to-group") {
              entity(as[PostDto]) { post =>
                complete(service.postToGroup(post))
              }
            },
            path("register-user") {
              entity(as[UserDto]) { user =>
                complete(service.registerUser(user))
              }
            }
          )
        }
      )
  }
}
