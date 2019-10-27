package com.example.groups.http

import java.util.UUID

import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.StandardRoute
import com.example.groups.Env
import com.example.groups.http.dto._
import com.example.groups.http.dto.JsonSupport._
import zio.{DefaultRuntime, ZIO}



case class Router(service: GroupService,repos: Env, runtime: DefaultRuntime) {


  def routes() = {
      concat(
        get {
          concat (
            path("list-groups") {
              parameters("userId".as[UUID]) { userId =>
                completeZio(service.listGroups(userId))
              }
            },
            path("group-feed") {
              parameters("userId".as[UUID], "groupId".as[Int], "start-from-post".as[UUID].?,  "number-posts-to-load".as[Int].?) {
               (userId, groupId, startFromTimestamp, numberPosts) =>
                 completeZio(service.groupFeed(userId, groupId, startFromTimestamp, numberPosts))
              }
            },
            path("all-groups-feed") {
              parameters("userId".as[UUID],"start-from-post".as[UUID].?,  "number-posts-to-load".as[Int].?) {
                (userId, startFromTimestamp, numberPosts) =>
                  completeZio(service.allGroupsFeed(userId, startFromTimestamp, numberPosts))
              }
            }
          )
        },
        post {
          concat(
            path("register-group-member") {
              entity(as[RegisterMemberDto]) { group =>
                completeZio(service.registerGroupMember(group))
              }
            },
            path("post-to-group") {
              entity(as[PostRequestDto]) { post =>
                completeZio(service.postToGroup(post))
              }
            },
            path("register-user") {
              entity(as[UserDto]) { user =>
                completeZio(service.registerUser(user))
              }
            }
          )
        }
      )
  }


  private def completeZio[A:ToResponseMarshaller ,B:ToResponseMarshaller](zio: => ZIO[Env,A, B]) = {
    val res: ZIO[Any, Nothing, StandardRoute] = zio.fold(complete(_) ,complete(_)).provide(repos)
    runtime.unsafeRun(res)
  }

}
