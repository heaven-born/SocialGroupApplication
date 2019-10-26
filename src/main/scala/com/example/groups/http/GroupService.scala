package com.example.groups.http

import com.example.groups.http.dto._
import com.example.groups.domain.Model._
import java.util.UUID
import java.util.UUID._

import com.example.groups.storage.AppDatabase
import zio.{IO, ZIO}

class GroupService (net: Network) {

  import net._

  def registerUser(user: UserDto):ZIO[AppDatabase,ErrorDto,UserRegisteredDto] = wrapError {
    for {
      registeredUser <- users.register(User(randomUUID, user.name))
    } yield UserRegisteredDto(registeredUser.id,s"User ${registeredUser.name} was registered")
  }

  def postToGroup(user: PostRequestDto):IO[ErrorDto,SuccessDto]  = wrapError {
    for {
      group <- groups.get(user.groupId)
      post = Post(randomUUID, now, user.content)
      _ <- group.post(post, user.userId, group.id)
    } yield SuccessDto(s"Message posted. ID: ${post.id}")
  }

  def registerGroupMember(newMember: RegisterMemberDto):ZIO[AppDatabase,ErrorDto,SuccessDto] = wrapError {
    for {
      group <- groups.get(newMember.groupId)
      _ <- group.registerMember(newMember.userId)
    } yield SuccessDto("Member was registered")
  }

  def listGroups(userId: UUID):ZIO[AppDatabase, ErrorDto,GroupsDto] = wrapError {
    for {
      groups <- groups.byUser(userId)
    } yield GroupsDto(groups.ids)
  }

  def groupFeed(userId: UUID, groupId: Int,
                startFromTimestamp: Option[Long],
                numberPostsToLoad: Option[Int]):IO[ErrorDto,FeedResponseDto] = wrapError {
    for {
      group <- groups.get(groupId, userId)
      feed <- group.feed(startFromTimestamp, numberPostsToLoad.getOrElse(POSTS_ON_PAGE))
      res = feed.map(p => PostResponseDto(p.post.id, p.post.creationTime, p.user.id, p.user.name, p.post.content))
    } yield FeedResponseDto(res)
  }


  def allGroupsFeed(userId: UUID, startFromTimestamp: Option[Long],
                    numberPostsToLoad: Option[Int]):ZIO[AppDatabase, ErrorDto,AllFeedsResponseDto] = wrapError {
    for {
      groupSet <- groups.byUser(userId)
      feed <- groupSet.feed(startFromTimestamp, numberPostsToLoad.getOrElse(POSTS_ON_PAGE))
      res = feed.map(p => PostAllFeedsResponseDto(p.post.id, p.post.creationTime, p.user.id, p.user.name.toString, p.post.content))
    } yield AllFeedsResponseDto(res)
  }


  private def wrapError[R, A](zio: ZIO[R,Throwable,A]) = zio.mapError(e=>{e.printStackTrace();ErrorDto(e.getMessage)})
  private val POSTS_ON_PAGE = 100
  private def now = System.currentTimeMillis()


}
