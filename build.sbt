ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "book-bot"
  )

val http4sVersion = "0.23.18"
val http4sBlaze = "0.23.14"

libraryDependencies ++= Seq(
  "com.bot4s" %% "telegram-core" % "5.7.0",
  "com.typesafe" % "config" % "1.4.2",
  "dev.zio" %% "zio" % "2.0.18",
  "dev.zio" %% "zio-interop-cats" % "23.1.0.0",
  "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % "3.9.0",
  "org.typelevel" %% "cats-effect" % "3.4.8"
)
