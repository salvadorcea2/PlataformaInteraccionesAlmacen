import sbtassembly.AssemblyKeys

name := "repositorio"


lazy val commonSettings = Seq(
  organization := "cl.exe",
  version := "1.0",
  scalaVersion := "2.12.2",
  test in assembly := {}

)


lazy val dockerSettings = Seq(
 // docker <<= (docker dependsOn (AssemblyKeys.assembly in comun)),

  dockerfile in docker := {
    val artifact: File = assembly.value
    val artifactTargetPath = s"/app/${artifact.name}"

    new Dockerfile {
      from("java")
      add(artifact, artifactTargetPath)
      expose(1600,1601,1602)
      entryPoint("java", "-jar", artifactTargetPath)
    }
  },

  imageNames in docker := Seq(
    // Sets the latest tag
    ImageName(s"${organization.value}/${name.value}:latest"),

    // Sets a name with a tag that contains the project version
    ImageName(
      repository = name.value,
      tag = Some("v" + version.value)
    )
  ),
  buildOptions in docker := BuildOptions(cache = false)
)




lazy val testVersion = "3.0.1"
lazy val postgresqlAsyncVersion = "0.2.21"
lazy val typesafeConfigVersion = "1.3.1"
lazy val akkaVersion = "2.5.3"
lazy val logbackVersion = "1.2.3"
lazy val apacheCamelVersion = "2.19.1"
lazy val scalaLoggingVersion = "3.7.1"
lazy val poiVersion = "3.16"

lazy val dependenciasComunes = Seq(
  "org.scala-lang" % "scala-library" % "2.12.2",
  "com.typesafe" % "config" % typesafeConfigVersion,
  "com.github.mauricio" %% "postgresql-async" % postgresqlAsyncVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-contrib" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "org.apache.poi" % "poi-ooxml" % poiVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "org.scalatest" %% "scalatest" % testVersion % "test"
)

lazy val dependenciasReceptor = Seq(
  "org.apache.camel" % "camel-core" % apacheCamelVersion  % "compile" exclude("org.slf4j", "slf4j-api"),
  "org.apache.camel" % "camel-ftp" % apacheCamelVersion
)

lazy val comun = project
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= dependenciasComunes)
  .settings(assemblyJarName in assembly := "comun.jar")

lazy val ejecutor = project.in(file("ejecutor"))
  .aggregate(comun)
  .dependsOn(comun)
  .settings(commonSettings:_*)
  .settings(libraryDependencies ++= dependenciasComunes)
  .settings(
    mainClass in assembly := Some("cl.exe.main.EjecutorMain"),
    assemblyJarName in assembly := "ejecutor.jar"
  )
  .enablePlugins(sbtdocker.DockerPlugin, JavaServerAppPackaging)
  .settings(dockerSettings)


lazy val administrador = project.in(file("administrador"))
  .aggregate(comun)
  .dependsOn(comun)
  .settings(commonSettings:_*)
  .settings(libraryDependencies ++= dependenciasComunes)
  .settings(
    mainClass in assembly := Some("cl.exe.main.AdministradorMain"),
    assemblyJarName in assembly := "administrador.jar"
  )
  .enablePlugins(sbtdocker.DockerPlugin, JavaServerAppPackaging)
  .settings(dockerSettings)

lazy val receptor = project.in(file("receptor"))
  .aggregate(comun)
  .dependsOn(comun)
  .settings(commonSettings:_*)
  .settings(libraryDependencies ++= dependenciasComunes)
  .settings(libraryDependencies ++= dependenciasReceptor)
  .settings(
    mainClass in assembly := Some("cl.exe.main.ReceptorMain"),
    assemblyJarName in assembly := "receptor.jar"
  )

  .enablePlugins(sbtdocker.DockerPlugin, JavaServerAppPackaging)
  .settings(dockerSettings)


lazy val repositorio = project.in( file(".") )
  .settings(commonSettings:_*)
  .aggregate(comun, ejecutor, administrador, receptor)

