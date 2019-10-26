package com.example.groups.domain

import java.util.UUID

import com.example.groups.domain.behaviour._

object Model {

  case class Network (groups: Groups = Groups() ,users: Users = Users())

  case class Groups() extends GroupsBehaviour
  case class Group(id: Int) extends GroupBehaviour
  case class GroupSet(ids:Set[Int]) extends GroupSetBehaviour

  case class Users() extends UsersBehaviour
  case class User(id: UUID, name: String)

  case class Post(id: UUID, creationTime: Long, content: String)


  // ------- wrappers -----------/

  case class PostWithAuthor(post: Post, user: User)

}
