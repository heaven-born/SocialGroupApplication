package com.example.groups.domain.behaviour

import com.example.groups.Env
import com.example.groups.domain.Model._
import zio.{RIO, ZIO}

trait UsersBehaviour {
  self: Users =>

  def register(user: User): RIO[Env,User] =
       ZIO.accessM[Env] { _.users.store(user) }




}
