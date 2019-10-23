package com.example.groups.http.dto

case class FeedResponseDto(posts: List[PostResponseDto])

case class AllFeedsResponseDto(posts: List[PostAllFeedsResponseDto])
