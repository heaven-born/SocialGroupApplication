package com.example.groups.http.dto

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import spray.json._

object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit lazy val groupsFormat = jsonFormat1(GroupsDto)
  implicit lazy val postFormat = jsonFormat3(PostDto)
  implicit lazy val orderFormat = jsonFormat1(GroupFeedDto)
  implicit lazy val registerMemberFormat = jsonFormat2(RegisterMemberDto)
  implicit lazy val userFormat = jsonFormat1(UserDto)
  implicit lazy val successFormat = jsonFormat1(ResultDto)

}
