package com.example.groups.storage

import java.time.Instant
import java.util.UUID
import com.datastax.driver.core.utils.UUIDs

import com.example.groups.domain.Model.{Group, Post, PostWithAuthor, User}
import com.example.groups.storage.Model.{group_member, post, user}
import com.outworkers.phantom.dsl._
import zio.{Task, ZIO}

import scala.concurrent.Future
import scala.util.Random

private [storage] object Model {
  case class group_member(user_id:UUID,group_id: Int)
  case class user(user_id: UUID, user_name: String)
  case class post( post_id: UUID,
                   group_id: Int,
                   shard_id: UUID,
                   user_id: UUID,
                   user_name :String,
                   content: String)
}

abstract class posts extends Table[posts,post] {
  object group_id extends IntColumn with PartitionKey
  object shard_id extends UUIDColumn with PartitionKey
  object post_id extends TimeUUIDColumn with PrimaryKey
  object user_id extends UUIDColumn
  object user_name extends StringColumn
  object content extends StringColumn

  //object posted_time extends Time with PrimaryKey
  //posted_time timestamp,
  val rnd = new Random()

  def store(post: Post, groupId: Int, userId: UUID, userName: String, shards: Set[UUID]): Task[Post] = ZIO.fromFuture { _ =>

    val n = util.Random.nextInt(shards.size)
    val randomShard = shards.iterator.drop(n).next

    insert
      .value(_.group_id, groupId)
      .value(_.shard_id, randomShard)
      .value(_.post_id, post.id)
      .value(_.user_id, userId)
      .value(_.user_name, userName)
      .value(_.content, post.content)
      .future().map(_=>post)
  }

  def findAllStartingFrom(timeUid: Option[UUID], groupId: Int, postLimit: Int, shards: Set[UUID]) = ZIO.fromFuture{ _ =>
    def fromShard(shardId: UUID) = {
      val common = select.where(_.group_id eqs groupId)
                         .and(_.shard_id eqs shardId)
                         .limit(postLimit)
      timeUid match {
        case None =>  common.fetch()
        case Some(uid) =>  common.and(_.post_id >= uid).fetch()
      }
    }


    for {
      posts <- Future.sequence(shards.map(fromShard)).map(_.flatten)
      res  = posts.map{p =>
          val createDateTimestamp = UUIDs.unixTimestamp(p.post_id) *1000L
          val ps = Post(p.post_id,p.group_id,createDateTimestamp,p.content)
          val user = User(p.user_id,p.user_name)
          PostWithAuthor(ps,user)
      }
    }  yield res

  }

}

abstract class users extends Table[users, user] {
  object user_id extends UUIDColumn with PartitionKey
  object user_name extends StringColumn

  def findById(id: UUID) = ZIO.fromFuture{ _ =>
    select.where(_.user_id eqs id).one().map(_.get)
  }.mapError(_=>new IllegalStateException(s"Cant find user with ID $id"))

  def exists(userId: UUID): Task[Boolean] = findById(userId).fold(_=>false, _=>true)

  def store(user: User): Task[User] = ZIO.fromFuture { _ =>
    insert
      .value(_.user_id, user.id)
      .value(_.user_name, user.name).future().map(_=>user)
  }
}

abstract class group_members extends Table[group_members, group_member] {
  object user_id extends UUIDColumn with PartitionKey
  object group_id extends IntColumn with PrimaryKey

  def store(groupId: Int, userId: UUID): Task[Group] = ZIO.fromFuture { _ =>
    insert
      .value(_.user_id, userId)
      .value(_.group_id, groupId)
      .future()
      .map(_=>Group(groupId))
  }

  def findByUserId(userId: UUID): Task[List[Group]]= ZIO.fromFuture { _ =>
    select.where(_.user_id eqs userId)
      .fetch()
      .map(_.map(grp=>Group(grp.group_id)))

  }
}


