package com.example.groups.http.dto

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.example.groups.utils.UUIDFormatter._
import spray.json._

object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit lazy val groupsFormat = jsonFormat1(GroupsDto)
  implicit lazy val postReqFormat = jsonFormat3(PostRequestDto)
  implicit lazy val registerMemberFormat = jsonFormat2(RegisterMemberDto)
  implicit lazy val userFormat = jsonFormat1(UserDto)
  implicit lazy val successFormat = jsonFormat1(SuccessDto)
  implicit lazy val errorFormat = jsonFormat1(ErrorDto)
  implicit lazy val userRegisteredFormat = jsonFormat2(UserRegisteredDto)

  implicit lazy val postRespFormat = jsonFormat5(PostResponseDto)
  implicit lazy val orderFormat = jsonFormat1(FeedResponseDto)

  implicit lazy val postRespAllFormat = jsonFormat6(PostAllFeedsResponseDto)
  implicit lazy val postAllRespFormat = jsonFormat1(AllFeedsResponseDto)

}
