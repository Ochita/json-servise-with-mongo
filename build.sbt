name := "json-servise-with-mongo"

version := "0.0.1"

scalaVersion := "2.11.8"

libraryDependencies += "com.typesafe.akka" % "akka-http-core_2.11" % "2.4.11"
libraryDependencies += "com.typesafe.akka" % "akka-http-testkit_2.11" % "2.4.11"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4.11"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.4.11"

libraryDependencies += "de.heikoseeberger" %% "akka-http-jackson" % "1.10.1"
libraryDependencies += "org.reactivemongo" %% "reactivemongo" % "0.12-RC5"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0"

libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.21"