name := "SocialGroupApplication"

version := "0.1"

scalaVersion := "2.12.0"

libraryDependencies += "com.github.nosan" % "embedded-cassandra" % "3.0.0"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.0-M8"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.10"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.0-M8"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.10"

libraryDependencies += "io.spray" %% "spray-json" % "1.3.5"


libraryDependencies += "dev.zio" %% "zio" % "1.0.0-RC15"

libraryDependencies += "io.spray" %% "spray-json" % "1.3.5"

libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.10"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.28"


libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.28"

libraryDependencies += "com.outworkers" %% "phantom-dsl" % "2.42.0"

libraryDependencies += "org.scalatest" %% "scalatest-matchers-core" % "3.2.0-M1" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0-M1" % Test
libraryDependencies += "org.scalatest" %% "scalatest-wordspec" % "3.2.0-M1" % Test








libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.23" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.10" % Test
)


//libraryDependencies ++= Seq(
  //"io.spray" % "spray-routing-shapeless2" % "1.3.5"
//)






