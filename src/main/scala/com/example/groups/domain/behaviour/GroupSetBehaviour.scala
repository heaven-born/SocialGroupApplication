package com.example.groups.domain.behaviour

import com.example.groups.domain.Model._
import zio.Task

trait GroupSetBehaviour {
  self: GroupSet =>

    def feed(startFromTimestamp: Option[Long], numberPostsToLoad: Int): Task[List[PostWithAuthor]] = ???

}
