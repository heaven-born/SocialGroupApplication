package com.example.groups.http

import java.util.UUID

import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.server.Directives._
import com.example.groups.http.dto._
import com.example.groups.http.dto.JsonSupport._
import io.getquill.{CassandraSyncContext, LowerCase}
import zio.internal.PlatformLive
import zio.{DefaultRuntime, IO, Runtime, ZIO}


case class Router(service: GroupService,ctx: CassandraSyncContext[LowerCase]) {

  //private val runtime = new DefaultRuntime {}
  val runtime = Runtime(ctx, PlatformLive.Default)


  private def completeZio[A:ToResponseMarshaller ,B:ToResponseMarshaller](zio: => ZIO[CassandraSyncContext[LowerCase],A, B]) =
    runtime.unsafeRun(zio.fold(complete(_),complete(_)))

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
              parameters("userId".as[UUID], "groupId".as[Int], "start-from-timestamp".as[Long].?,  "number-posts-to-load".as[Int].?) {
               (userId, groupId, startFromTimestamp, numberPosts) =>
                 completeZio(service.groupFeed(userId, groupId, startFromTimestamp, numberPosts))
              }
            },
            path("all-user-groups-feed") {
              parameters("userId".as[UUID],"start-from-timestamp".as[Long].?,  "number-posts-to-load".as[Int].?) {
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
}
