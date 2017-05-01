import sbt._
import Keys._

object Dependencies {

/*
  val commonDependencies: Seq[ModuleID] = Seq(
    slick,
    slf4jNop
  )
*/

  val serverDependencies: Seq[ModuleID] = Seq("com.typesafe.akka" %% "akka-http-core" % "10.0.5",
    "com.typesafe.akka" %% "akka-http" % "10.0.5",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.0.5",
    //"com.typesafe.akka" %% "akka-http-spray-json" % "10.0.5",
    "com.typesafe.akka" %% "akka-http-jackson" % "10.0.5",
    //"com.typesafe.akka" %% "akka-http-xml" % "10.0.5",
    "org.asynchttpclient" % "async-http-client" % "2.0.31",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.8",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.8"
  )
}