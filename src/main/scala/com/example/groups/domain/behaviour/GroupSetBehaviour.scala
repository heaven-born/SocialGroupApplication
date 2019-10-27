package com.example.groups.domain.behaviour

import java.util.UUID

import com.example.groups.Main.Env
import com.example.groups.domain.Model._
import zio.{RIO, ZIO}


trait GroupSetBehaviour {
  groupSet: GroupSet =>

    def feed(startFromPostId: Option[UUID], numberOfPostsToLoad: Int): RIO[Env,List[PostWithAuthor]] = {
        val groups = groupSet.ids.map(Group)
        val posts = groups.map(_.feed(startFromPostId,numberOfPostsToLoad))

      for {
         nestedPosts <- ZIO.collectAllPar(posts)
         posts = nestedPosts
           .flatten
           .sortBy(_.post.creationTime)// \
           .take(numberOfPostsToLoad) // -- doesn't look like a good solution, but I could not find better
      } yield posts
    }

}
