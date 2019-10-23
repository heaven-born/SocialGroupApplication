package com.example.groups.http

import com.example.groups.http.dto._

class GroupService {

  private val POST_ON_PAGE = Some(100)

  def registerUser(user: UserDto):ResultDto = ResultDto("User registered")

  def postToGroup(user: PostDto):ResultDto = ResultDto("Message posted")

  def registerGroupMember(newMember: RegisterMemberDto):ResultDto = ResultDto("Member registered")

  def listGroups():GroupsDto = GroupsDto(Set(1,2,3))

  def groupFeed(groupId: Long, startFromPostId: Option[Long], numberPostsToLoad: Option[Int] = POST_ON_PAGE):GroupFeedDto =
    GroupFeedDto(List(PostDto(1,2,"postMessage from one groupe")))

  def allGroupsFeed(startFromPostId: Option[Long], numberPostsToLoad: Option[Int] =  POST_ON_PAGE):GroupFeedDto =
    GroupFeedDto(List(PostDto(1,2,"postMessage from all groups")))

}
