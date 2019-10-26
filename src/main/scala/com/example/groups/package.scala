package com.example

import io.getquill.context.cassandra.encoding.Decoders
import io.getquill.{CassandraSyncContext, LowerCase}

package object groups {
  type CTX = CassandraSyncContext[LowerCase]

}
