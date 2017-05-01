import sbt._
import Dependencies._

lazy val commonSettings = Seq(
  organization := "com.example",
  version := "1.0",
  scalaVersion := "2.12.2"
)

lazy val root = (project in file("."))
  .aggregate(webSocketServer)


lazy val webSocketServer = (project in file("web-socket-server"))
  .settings(
    commonSettings
  )
  .settings(
    libraryDependencies ++= serverDependencies
  )