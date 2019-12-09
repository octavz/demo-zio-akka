scalaVersion := "2.12.10"
name := "demo-zio"
organization := "com.example"
version := "1.0"

val ZioVersion = "1.0.0-RC17"
val ZioCatsVersion = "2.0.0.0-RC10"
val CirceVersion = "0.12.3"
val AkkaHttpVersion = "10.1.11"
val AkkaHttpCirce = "1.29.1"
val PureconfigVersion = "0.12.1"
val LogbackVersion = "1.2.3"
val CatsEffectVersion = "2.0.0"
val AkkaStreamVersion = "2.5.26"
val FlywayVersion = "6.1.0"
val DoobieVersion = "0.8.6"

libraryDependencies ++=  Seq(
  "dev.zio" %% "zio" % ZioVersion,
  "dev.zio" %% "zio-interop-cats" % ZioCatsVersion,
  "com.typesafe.akka"     %% "akka-http"           % AkkaHttpVersion,
  "com.typesafe.akka"     %% "akka-stream"         % AkkaStreamVersion,
  "de.heikoseeberger"     %% "akka-http-circe"     % AkkaHttpCirce,
  "io.circe"              %% "circe-core"          % CirceVersion,
  "io.circe"              %% "circe-parser"        % CirceVersion,
  "io.circe"              %% "circe-generic"       % CirceVersion,
  "com.github.pureconfig" %% "pureconfig"          % PureconfigVersion,
  "ch.qos.logback"        %  "logback-classic"     % LogbackVersion,
  "org.typelevel"         %% "cats-effect"         % CatsEffectVersion,
  "org.flywaydb"          %  "flyway-core"         % FlywayVersion,
  "org.tpolecat"          %% "doobie-core"         % DoobieVersion,
  "org.tpolecat"          %% "doobie-postgres"     % DoobieVersion,
  "org.tpolecat"          %% "doobie-hikari"       % DoobieVersion,
)

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Ypartial-unification",
  // "-Xfatal-warnings",
)

addCompilerPlugin("com.olegpy"     %% "better-monadic-for" % "0.3.1") 

addCompilerPlugin("com.github.cb372" % "scala-typed-holes" % "0.1.1" cross CrossVersion.full)
