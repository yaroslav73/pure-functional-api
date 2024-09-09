val scala3Version = "3.5.0"
val PekkoVersion = "1.0.2"
val PekkoHttpVersion = "1.0.1"
val circeVersion = "0.14.10"

lazy val root = project
  .in(file("."))
  .settings(
    name := "pure-functional-api",
    version := "0.1",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-actor-typed" % PekkoVersion,
      "org.apache.pekko" %% "pekko-stream" % PekkoVersion,
      "org.apache.pekko" %% "pekko-http" % PekkoHttpVersion,

      "com.typesafe.slick" %% "slick" % "3.5.1",

      "eu.timepit" %% "refined" % "0.11.2",

      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,

      "org.scalameta" %% "munit" % "1.0.0" % Test
    )
  )
