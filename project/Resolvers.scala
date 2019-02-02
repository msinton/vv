import sbt._

object Resolvers {

  val confluentPlatform = "Confluent Platform" at "http://packages.confluent.io/maven/"
  val artima            = "Artima Maven Repository" at "http://repo.artima.com/releases"
  val commercetools     = Resolver.bintrayRepo("commercetools", "maven")
  val hseeberger        = Resolver.bintrayRepo("hseeberger", "maven")
}
