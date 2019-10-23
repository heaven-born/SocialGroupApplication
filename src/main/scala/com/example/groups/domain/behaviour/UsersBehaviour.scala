package com.example.groups.domain.behaviour

import com.example.groups.domain.Model._
import zio.Task

trait UsersBehaviour {
  self: Users =>

    def register(user: User):Task[User] = ???

}
