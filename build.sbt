name := "SocialGroupApplication"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies += "com.github.nosan" % "embedded-cassandra" % "3.0.0"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.0-M8"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.10"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.0-M8"

libraryDependencies += "io.spray" %% "spray-json" % "1.3.5"

libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.10"
