package com.example.groups.http

import com.example.groups.http.dto._

class GroupService {

  private val POST_ON_PAGE = 100

  def registerUser(user: UserDto):Unit = ???

  def addPost(user: PostDto):Unit= ???

  def addGroupMember(newMember: RegisterMemberDto):Unit = ???

  def listGroups():GroupsDto = ???

  def groupFeed(groupId: Long, startFromPostId: Option[Long], numberPostsToLoad: Int = POST_ON_PAGE):GroupFeedDto = ???

  def allGroupsFeed(groupId: Long, startFromPostId: Option[Long], numberPostsToLoad: Int =  POST_ON_PAGE):GroupFeedDto = ???

}
