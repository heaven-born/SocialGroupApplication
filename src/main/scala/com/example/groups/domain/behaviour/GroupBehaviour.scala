package com.example.groups.domain.behaviour

import java.util.UUID

import com.example.groups.domain.Model._
import com.example.groups.storage.AppDatabase
import zio.{RIO, Task, ZIO}

trait GroupBehaviour {
  group : Group =>

      def feed(startFromTimestamp: Option[Long], numberPostsToLoad: Int):Task[List[PostWithAuthor]] =
        ZIO.effectTotal(
          List(
            PostWithAuthor(
              Post(UUID.randomUUID(),System.currentTimeMillis(),"content stub"),
              User(UUID.randomUUID(),"userName"))))

      def registerMember(userId: UUID):RIO[AppDatabase,Group] =
        for {
          exists <- ZIO.accessM[AppDatabase] { _.users.exists(userId) }
          res <- if (!exists)
                   ZIO.fail(new IllegalStateException(s"User with ID $userId doesn't exist"))
                 else
                   ZIO.accessM[AppDatabase] { _.groups.store(group.id,userId) }
        } yield res

      def post(post: Post, userId: UUID, groupId: Int):Task[Post] =
        ZIO.effectTotal(Post(UUID.randomUUID(),System.currentTimeMillis(),"content stub"))




}
