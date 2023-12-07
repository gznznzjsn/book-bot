ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

val http4sVersion = "0.23.18"
val http4sBlaze = "0.23.14"
val zioVersion = "2.0.13" // Scala library for asynchronous and concurrent programming
val zioMetricsConnectorsVersion = "2.0.8"
val zioJsonVersion = "0.5.0"
val postgresVersion = "42.5.4" // Java database connectivity (JDBC) driver for PostgreSQL
val zioQuillVersion = "4.6.0" // compile-time database query library for ZIO
val flywayVersion = "9.16.0" // manages database migrations

Global / onChangedBuildSource := ReloadOnSourceChanges


val sharedSettings = Seq(
  libraryDependencies ++= Seq(
    "dev.zio" %% "zio-json" % zioJsonVersion
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "utf8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-Xfatal-warnings",
    "-Ymacro-annotations"
  )
)


lazy val root = (project in file("."))
  .settings(
    name := "book-bot",
    libraryDependencies ++= Seq(
      "com.bot4s" %% "telegram-core" % "5.7.0",
      "com.typesafe" % "config" % "1.4.2",
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-macros" % zioVersion,
      "dev.zio" %% "zio-metrics-connectors" % zioMetricsConnectorsVersion,
      "dev.zio" %% "zio-test" % zioVersion % Test,
      "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
      "dev.zio" %% "zio-interop-cats" % "23.1.0.0",
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % "3.9.0",
      "org.typelevel" %% "cats-effect" % "3.4.8",
      "io.getquill" %% "quill-jdbc-zio" % zioQuillVersion,
      "org.postgresql" % "postgresql" % postgresVersion,
      "org.flywaydb" % "flyway-core" % flywayVersion,

    ),
    Test / fork := true,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
  .enablePlugins(JavaAppPackaging)
  .settings(sharedSettings)
  .enablePlugins(FlywayPlugin)




