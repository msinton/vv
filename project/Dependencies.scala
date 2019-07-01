import sbt._

object Dependencies {

  object V {
    val logback       = "1.1.7"
    val scalaLogging  = "3.5.0"
    val scalactic     = "3.0.5"
    val scalatest     = "3.0.5"
    val akka          = "10.0.6"
    val akkaTyped          = "2.5.20"
    val httpJson4s    = "1.14.0"
    val json4s        = "3.5.2"
    val jacksonMapper = "1.9.13"
    val base64        = "0.2.4"
    val diode         = "1.1.2"
    val mongoDriver   = "2.1.0"
  }

  object L {
    val logback       = "ch.qos.logback"             % "logback-classic"     % V.logback
    val scalaLogging  = "com.typesafe.scala-logging" %% "scala-logging"      % V.scalaLogging
    val scalactic     = "org.scalactic"              %% "scalactic"          % V.scalactic
    val scalatest     = "org.scalatest"              %% "scalatest"          % V.scalatest
    val akkaHttp      = "com.typesafe.akka"          %% "akka-http"          % V.akka
    val akkaTestkit   = "com.typesafe.akka"          %% "akka-http-testkit"  % V.akka
    val akkaTyped = "com.typesafe.akka" %% "akka-actor-typed" % V.akkaTyped
    val json4s        = "org.json4s"                 %% "json4s-native"      % V.json4s
    val httpJson4s    = "de.heikoseeberger"          %% "akka-http-json4s"   % V.httpJson4s
    val jacksonMapper = "org.codehaus.jackson"       % "jackson-mapper-asl"  % V.jacksonMapper
    val base64        = "com.github.marklister"      %% "base64"             % V.base64
    val diode         = "io.suzaku"                  %% "diode"              % V.diode
    val mongoDriver   = "org.mongodb.scala"          %% "mongo-scala-driver" % V.mongoDriver
  }

  val lib = Seq(
    L.logback,
    L.scalaLogging,
    L.scalactic,
    L.akkaHttp,
    L.httpJson4s,
    L.json4s,
    L.jacksonMapper,
    L.diode,
    L.base64,
  ) ++ Seq(
    L.akkaTestkit,
    L.scalatest
  ).map(_ % Test) ++ Seq(
    L.akkaTestkit,
    L.scalatest
  ).map(_ % IntegrationTest)

  val server = Seq(
    L.logback,
    L.scalaLogging,
    L.scalactic,
    L.akkaHttp,
    L.httpJson4s,
    L.json4s,
    L.mongoDriver,
  ) ++ Seq(
    L.akkaTestkit,
    L.scalatest
  ).map(_ % Test) ++ Seq(
    L.akkaTestkit,
    L.scalatest
  ).map(_ % IntegrationTest)
}
