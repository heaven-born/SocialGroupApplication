package com.example.groups.domain.behaviour

import com.example.groups.CTX
import com.example.groups.domain.Model._
import zio.{RIO, Task, ZIO}

trait UsersBehaviour {
  self: Users =>

  def register(user: User): RIO[CTX,User] =
       ZIO.accessM[CTX] { ctx => registerM(user)(ctx) }


  private def registerM(user: User)(ctx: CTX): Task[User] = ZIO.effect {
    //assuming that there can be multiple users with the same name but unique ID
    import ctx._
    ctx.run {
      quote {
        querySchema[User]("user", _.id -> "user_id", _.name -> "user_name")
          .insert(lift(user))
      }
    }

    user
  }


}
