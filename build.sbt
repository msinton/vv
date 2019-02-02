import sbt.Keys.{resolvers, scalacOptions}
import sbt.{Resolver, inThisBuild}
logLevel := Level.Info

name := "vv"

inThisBuild(
  Seq(
    version := "0.1.0"
  ) ++ BuildSettings.formattingSettings
)

lazy val commonSettings = Seq(
  organization := "com.consideredgames",
  scalaVersion := BuildSettings.scalaVersion,
  scalacOptions := BuildSettings.compilerOptions,
  javacOptions ++= BuildSettings.javaCompilerOptions,
  resolvers ++= Seq(
    Resolvers.confluentPlatform,
    Resolvers.artima,
    Resolvers.commercetools,
    Resolvers.hseeberger
  )
)

lazy val lib = project
  .in(file("lib"))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    libraryDependencies ++= Dependencies.lib,
    name := "vv-lib",
    Defaults.itSettings
  )

lazy val server = project
  .in(file("server"))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    resolvers ++= Seq(Resolver.mavenLocal),
    libraryDependencies ++= Dependencies.server,
    Defaults.itSettings
  )
  .dependsOn(lib)
