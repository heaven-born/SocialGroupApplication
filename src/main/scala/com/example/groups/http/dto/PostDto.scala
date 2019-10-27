package com.example.groups.http.dto

import java.util.UUID

case class PostRequestDto(userId: UUID, groupId: Int, content: String)

case class PostResponseDto(id: UUID, creationTime: Long, authorUserId: UUID, authorUserName: String, content: String)
case class PostAllFeedsResponseDto(id: UUID, creationTime: Long, authorUserId: UUID, authorUserName: String, content: String, groupId: Int)
