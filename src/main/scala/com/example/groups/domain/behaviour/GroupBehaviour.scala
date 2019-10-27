package com.example.groups.domain.behaviour

import java.util.UUID

import com.example.groups.Main.Env
import com.example.groups.domain.Model._
import zio.{RIO, Task, ZIO}

trait GroupBehaviour {
  group : Group =>

      def feed(startFromPostId: Option[UUID], numberPostsToLoad: Int):Task[List[PostWithAuthor]] =
        ZIO.effectTotal(
          List(
            PostWithAuthor(
              Post(UUID.randomUUID(),System.currentTimeMillis(),"content stub"),
              User(UUID.randomUUID(),"userName"))))

      def registerMember(userId: UUID):RIO[Env,Group] =
        for {
          exists <- ZIO.accessM[Env] { _.users.exists(userId) }
          res <- if (!exists)
                   ZIO.fail(new IllegalStateException(s"User with ID $userId doesn't exist"))
                 else
                   ZIO.accessM[Env] { _.groups.store(group.id,userId) }
        } yield res

      def post(post: Post, userId: UUID, groupId: Int):RIO[Env,Post] =
        for {
          user <- ZIO.accessM[Env] { _.users.findById(userId) }
          allShards <- ZIO.accessM[Env] { env => env.shards.get }
          res <- ZIO.accessM[Env] { env =>
            val shards = allShards.getOrElse(groupId,Set(env.defaultShard))
            env.posts.store(post,groupId,userId,user.user_name,shards)
          }
        } yield res




}
