organization := "com.consideredgames"
name := "vv"

version := "1.0"

lazy val commonSettings = Seq(
  scalaVersion := "2.11.11",

  scalacOptions += "-feature",
  scalacOptions += "-Ylog-classpath",

  resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases",
  resolvers += Resolver.bintrayRepo("commercetools", "maven"),
  resolvers += Resolver.bintrayRepo("hseeberger", "maven"),

  libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7",

  libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",

  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % "10.0.6",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.0.6" % "it,test"
  ),
  libraryDependencies += "de.heikoseeberger" %% "akka-http-json4s" % "1.14.0",
  libraryDependencies += "org.json4s" %% "json4s-native" % "3.5.2",


  libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "it,test" // can't use IntegrationTest, Test in this version of sbt :(
)

lazy val lib = project.in(file("lib"))
  .configs(IntegrationTest)
  .settings(
    commonSettings,

    resolvers += "softprops-maven" at "http://dl.bintray.com/content/softprops/maven",

    libraryDependencies += "me.lessis" %% "base64" % "0.2.0",

    libraryDependencies += "org.codehaus.jackson" % "jackson-mapper-asl" % "1.9.13",

    Defaults.itSettings
  )

lazy val server = project.in(file("server"))
  .configs(IntegrationTest)
  .settings(
    commonSettings,

    resolvers += Resolver.mavenLocal,

    libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.1.0",

    Defaults.itSettings

    //libraryDependencies += "com.consideredgames" %% "vv-lib" % "0.1-SNAPSHOT"

  ).dependsOn(lib)


