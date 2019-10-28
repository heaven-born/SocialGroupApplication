package com.example.groups.domain.behaviour

import java.util.UUID

import com.example.groups.Env
import com.example.groups.domain.Model._
import zio.{RIO, ZIO}

trait GroupBehaviour {
  group : Group =>

      def feed(startFromPostId: Option[UUID], numberPostsToLoad: Int):RIO[Env,Seq[PostWithAuthor]] = {
        for {
          posts <- ZIO.accessM[Env] {  env =>
            val groupShards = env.shardMapUnsafe.getOrElse(group.id,Seq(env.defaultShard))
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
          res <- ZIO.accessM[Env] { env =>
            val shards = env.shardMapUnsafe.getOrElse(group.id,Seq(env.defaultShard))
            env.posts.store(post,group.id,userId,user.user_name,shards)
          }
        } yield res




}
