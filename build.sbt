scalaVersion := "3.3.0"

val http4sVersion = "1.0.0-M40"
val log4catsVersion = "2.6.0"

// Only necessary for SNAPSHOT releases
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-ember-server" % http4sVersion,
  "org.http4s" %% "http4s-ember-client" % http4sVersion,
  "org.typelevel" %% "log4cats-slf4j" % log4catsVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "io.circe" %% "circe-generic" % "0.14.6",
  "org.reactivemongo" %% "reactivemongo" % "1.1.0-RC12",
)
