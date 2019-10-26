package com.example.groups.http.dto

import java.util.UUID

case class UserRegisteredDto(userId: UUID, message:String = "Success")
case class SuccessDto(message:String = "Success")
case class ErrorDto(message:String = "Error")
