package com.example.groups.domain.behaviour

import java.util.UUID

import com.example.groups.Env
import com.example.groups.domain.Model._
import zio.{RIO, Task, ZIO}

trait GroupBehaviour {
  group : Group =>

      def feed(startFromPostId: Option[UUID], numberPostsToLoad: Int):RIO[Env,Set[PostWithAuthor]] = {
        for {
          allShards <- ZIO.accessM[Env] { env => env.shards.get }
          posts <- ZIO.accessM[Env] {  env =>
            val groupShards = allShards.getOrElse(group.id,Set(env.defaultShard))
            env.posts.findAllStartingFrom(startFromPostId,group.id,numberPostsToLoad,groupShards) }
        } yield posts
      }

      def registerMember(userId: UUID):RIO[Env,Group] =
        for {
          exists <- ZIO.accessM[Env] { _.users.exists(userId) }
          res <- if (!exists)
                   ZIO.fail(new IllegalStateException(s"User with ID $userId doesn't exist"))
                 else
                   ZIO.accessM[Env] { _.groups.store(group.id,userId) }
        } yield res

      def post(post: Post, userId: UUID):RIO[Env,Post] =
        for {
          user <- ZIO.accessM[Env] { _.users.findById(userId) }
          allShards <- ZIO.accessM[Env] { env => env.shards.get }
          res <- ZIO.accessM[Env] { env =>
            val shards = allShards.getOrElse(group.id,Set(env.defaultShard))
            env.posts.store(post,group.id,userId,user.user_name,shards)
          }
        } yield res




}
