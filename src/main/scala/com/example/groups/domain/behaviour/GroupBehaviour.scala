package com.example.groups.domain.behaviour

import java.util.UUID

import com.example.groups.domain.Model._
import zio.{Task, ZIO}

trait GroupBehaviour {
  self : Group =>

      def feed(startFromTimestamp: Option[Long], numberPostsToLoad: Int):Task[List[PostWithAuthor]] =
        ZIO.effectTotal(
          List(
            PostWithAuthor(
              Post(UUID.randomUUID(),System.currentTimeMillis(),"content stub"),
              User(UUID.randomUUID(),"userName"))))

      def registerMember(userId: UUID):Task[Group] =
        ZIO.effectTotal(Group(UUID.randomUUID()))

      def post(post: Post, userId: UUID, groupId: UUID):Task[Post] =
        ZIO.effectTotal(Post(UUID.randomUUID(),System.currentTimeMillis(),"content stub"))

}
