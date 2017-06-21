name := "repositorio"


lazy val commonSettings = Seq(
  organization := "cl.exe",
  version := "1.0",
  scalaVersion := "2.12.2"
)

enablePlugins(DockerPlugin, JavaAppPackaging)

lazy val testVersion = "3.0.1"
lazy val postgresqlAsyncVersion = "0.2.21"
lazy val typesafeConfigVersion = "1.3.1"

lazy val commonDependencies = Seq(
  "com.typesafe" % "config" % typesafeConfigVersion,
  "com.github.mauricio" %% "postgresql-async" % postgresqlAsyncVersion,
  "org.scalatest" %% "scalatest" % testVersion % "test"
)

lazy val comun = project
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= commonDependencies)

lazy val repositorio = project.in( file(".") )
  .settings(commonSettings:_*)
  .settings(libraryDependencies ++= commonDependencies)
  .aggregate(comun)