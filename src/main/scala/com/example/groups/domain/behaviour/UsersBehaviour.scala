package com.example.groups.domain.behaviour

import com.example.groups.domain.Model._
import com.example.groups.storage.dto.AppDatabase
import zio.{RIO, Task, ZIO}

trait UsersBehaviour {
  self: Users =>

  def register(user: User): RIO[AppDatabase,User] =
       ZIO.accessM[AppDatabase] { _.users.register(user) }




}
