package com.example.groups.http

import com.example.groups.http.dto._
import com.example.groups.domain.Model._
import java.util.UUID
import java.util.UUID._

import com.datastax.driver.core.utils.UUIDs
import com.example.groups.Main.Env
import zio.ZIO

class GroupService (net: Network) {

  import net._

  def registerUser(user: UserDto):ZIO[Env,ErrorDto,UserRegisteredDto] = wrapError {
    for {
      registeredUser <- users.register(User(randomUUID, user.name))
    } yield UserRegisteredDto(registeredUser.id,s"User ${registeredUser.name} was registered")
  }

  def postToGroup(postRequest: PostRequestDto):ZIO[Env,ErrorDto,SuccessDto]  = wrapError {
    for {
      group <- groups.get(postRequest.groupId, postRequest.userId)
      post = Post(UUIDs.timeBased(),group.id, now, postRequest.content)
      _ <- group.post(post, postRequest.userId)
    } yield SuccessDto(s"Message posted. ID: ${post.id}")
  }

  def registerGroupMember(newMember: RegisterMemberDto):ZIO[Env,ErrorDto,SuccessDto] = wrapError {
    for {
      group <- groups.get(newMember.groupId)
      _ <- group.registerMember(newMember.userId)
    } yield SuccessDto("Member was registered")
  }

  def listGroups(userId: UUID):ZIO[Env, ErrorDto,GroupsDto] = wrapError {
    for {
      groups <- groups.byUser(userId)
    } yield GroupsDto(groups.ids)
  }

  def groupFeed(userId: UUID, groupId: Int,
                startFromPostId: Option[UUID],
                numberPostsToLoad: Option[Int]):ZIO[Env,ErrorDto,FeedResponseDto] = wrapError {
    for {
      group <- groups.get(groupId, userId)
      feed <- group.feed(startFromPostId, numberPostsToLoad.getOrElse(POSTS_ON_PAGE))
      res = feed.map(p => PostResponseDto(p.post.id, p.post.creationTime, p.user.id, p.user.name, p.post.content))
    } yield FeedResponseDto(res.toList)
  }


  def allGroupsFeed(userId: UUID, startFromTimestamp: Option[UUID],
                    numberPostsToLoad: Option[Int]):ZIO[Env, ErrorDto,AllFeedsResponseDto] = wrapError {
    for {
      groupSet <- groups.byUser(userId)
      feed <- groupSet.feed(startFromTimestamp, numberPostsToLoad.getOrElse(POSTS_ON_PAGE))
      res = feed.map(p => PostAllFeedsResponseDto(p.post.id, p.post.creationTime, p.user.id, p.user.name.toString, p.post.content,p.post.groupId))
    } yield AllFeedsResponseDto(res)
  }


  private def wrapError[R, A](zio: ZIO[R,Throwable,A]) = zio.mapError(e=>{e.printStackTrace();ErrorDto(e.getMessage)})
  private val POSTS_ON_PAGE = 100
  private def now = System.currentTimeMillis()


}
