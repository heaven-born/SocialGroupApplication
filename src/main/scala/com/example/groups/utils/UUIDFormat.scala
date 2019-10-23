package com.example.groups.utils

import java.util.UUID

import spray.json.{DeserializationException, JsString, JsValue, JsonFormat}

object UUIDFormatter {

  implicit object UUIDFormat extends JsonFormat[UUID] {
    def write(uuid: UUID) = JsString(uuid.toString)

    def read(value: JsValue) = {
      value match {
        case JsString(uuid) => UUID.fromString(uuid)
        case _ => throw new DeserializationException("Expected hexadecimal UUID string")
      }
    }
  }

}
