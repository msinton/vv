package com.consideredgames.server.db

import org.mongodb.scala.MongoClient

object Mongo {
  val mongoClient: MongoClient = MongoClient("mongodb://localhost")
}
