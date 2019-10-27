package com.example


import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.example.groups.domain.Model.Network
import com.example.groups.http.{GroupService, Router}
import com.example.groups.http.dto._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import com.example.groups.http.dto.JsonSupport._
import zio.DefaultRuntime

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}


class BasicTest extends AnyWordSpec with ScalatestRouteTest {


    import DefaultTestEvn._

    val runtime = new DefaultRuntime{}

    val smallRoute = Router(new GroupService(Network()),env,runtime).routes()

    {  // create db is doesn't exist yet
        import com.outworkers.phantom.dsl._
        val ec: ExecutionContextExecutor = ExecutionContext.global
        env.drop(env.defaultTimeout)(ec)
        env.create(env.defaultTimeout)(ec)
    }

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
                responseAs[SuccessDto].message should startWith ("Message posted. ID:")
            }

            val post2 = PostRequestDto(userId, 1, "some content")
            Post("/post-to-group", post2) ~> smallRoute ~> check {
                responseAs[ErrorDto].error should not be empty
            }

            Get(s"/group-feed?userId=$userId&groupId=2") ~> smallRoute ~> check {
                val feed = responseAs[FeedResponseDto]
                feed.posts.size shouldBe 1
            }

            Get(s"/group-feed?userId=$userId&groupId=1") ~> smallRoute ~> check {
                responseAs[ErrorDto].error should include ("is not a member of group")
            }

            Get(s"/all-groups-feed?userId=$userId") ~> smallRoute ~> check {
                val feed = responseAs[FeedResponseDto]
                feed.posts.size shouldBe 1
            }


        }
    }

}
