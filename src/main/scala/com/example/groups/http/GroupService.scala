package com.example.groups.http

import com.example.groups.http.dto._
import com.example.groups.domain.Model._
import java.util.UUID
import java.util.UUID._

import com.example.groups.CTX
import zio.{IO,ZIO}

class GroupService (net: Network) {

  import net._

  def registerUser(user: UserDto):ZIO[CTX,ErrorDto,SuccessDto] = wrapError {
    for {
      registeredUser <- users.register(User(randomUUID, user.name))
    } yield SuccessDto(s"User ${registeredUser.name} with ID ${registeredUser.id} was registered")
  }


  def postToGroup(user: PostRequestDto):IO[ErrorDto,SuccessDto]  = wrapError {
    for {
      group <- groups.get(user.groupId)
      post = Post(randomUUID, now, user.content)
      _ <- group.post(post, user.userId, group.id)
    } yield SuccessDto(s"Message posted. ID: ${post.id}")
  }

  def registerGroupMember(newMember: RegisterMemberDto):IO[ErrorDto,SuccessDto] = wrapError {
    for {
      group <- groups.get(newMember.groupId)
      _ <- group.registerMember(newMember.userId)
    } yield SuccessDto("Member was registered")
  }

  def listGroups():IO[ErrorDto,GroupsDto] = wrapError {
    for {
      groups <- groups.list()
      groupIds = groups.map(_.id)
    } yield GroupsDto(groupIds)
  }

  def groupFeed(userId: UUID, groupId: UUID,
                startFromTimestamp: Option[Long],
                numberPostsToLoad: Option[Int]):IO[ErrorDto,FeedResponseDto] = wrapError {
    for {
      group <- groups.get(groupId, userId)
      feed <- group.feed(startFromTimestamp, numberPostsToLoad.getOrElse(POSTS_ON_PAGE))
      res = feed.map(p => PostResponseDto(p.post.id, p.post.creationTime, p.user.id, p.user.name, p.post.content))
    } yield FeedResponseDto(res)
  }


  def allGroupsFeed(userId: UUID, startFromTimestamp: Option[Long],
                    numberPostsToLoad: Option[Int]):IO[ErrorDto,AllFeedsResponseDto] = wrapError {
    for {
      groupSet <- groups.byUser(userId)
      feed <- groupSet.feed(startFromTimestamp, numberPostsToLoad.getOrElse(POSTS_ON_PAGE))
      res = feed.map(p => PostAllFeedsResponseDto(p.post.id, p.post.creationTime, p.user.id, p.user.name.toString, p.post.content))
    } yield AllFeedsResponseDto(res)
  }


  private def wrapError[R, A](zio: ZIO[R,Throwable,A]) = zio.mapError(e=>ErrorDto(e.getMessage))
  private val POSTS_ON_PAGE = 100
  private def now = System.currentTimeMillis()


}
