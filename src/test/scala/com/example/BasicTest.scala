package com.example

import java.util.UUID

import com.example.groups.{Env, Main}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.datastax.driver.core.SocketOptions
import com.example.groups.domain.Model.Network
import com.example.groups.http.{GroupService, Router}
import com.example.groups.http.dto._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import com.example.groups.http.dto.JsonSupport._
import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoint}
import zio.{Ref, ZIO}


class BasicTest extends AnyWordSpec with ScalatestRouteTest {


    import DefaultTestEvn._

    val smallRoute = Router(new GroupService(Network()),env).routes()

    "Application" should {

        "pass basic flow test" in {

            val user = UserDto(name="username1")

            val userId = Post("/register-user", user) ~> smallRoute ~> check {
                val result = responseAs[UserRegisteredDto]
                result.message shouldEqual "User username1 was registered"
                result.userId
            }

            Get(s"/list-groups?userId=$userId") ~> smallRoute ~> check {
                val groups = responseAs[GroupsDto]
                groups.groupIds shouldEqual Set()
            }

            val groupMember = RegisterMemberDto(userId=userId,groupId = 2)

            Post("/register-group-member", groupMember) ~> smallRoute ~> check {
                responseAs[SuccessDto]
            }

            Post("/register-group-member", groupMember) ~> smallRoute ~> check {
                responseAs[SuccessDto]
            }

            Get(s"/list-groups?userId=$userId") ~> smallRoute ~> check {
                val groups = responseAs[GroupsDto]
                groups.groupIds shouldEqual Set(2)
            }


            val post = PostRequestDto(userId, 2, "some content")
            Post("/post-to-group", post) ~> smallRoute ~> check {
                responseAs[SuccessDto]
            }


        }
    }

}
