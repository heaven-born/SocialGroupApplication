package com.example.groups.http.dto

case class FeedResponseDto(posts: Seq[PostResponseDto])

case class AllFeedsResponseDto(posts: List[PostAllFeedsResponseDto])
