package com.example.groups.domain.behaviour

import com.example.groups.Main.AppDatabase
import com.example.groups.domain.Model._
import zio.{RIO, ZIO}

trait UsersBehaviour {
  self: Users =>

  def register(user: User): RIO[AppDatabase,User] =
       ZIO.accessM[AppDatabase] { _.users.store(user) }




}
