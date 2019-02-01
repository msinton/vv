package com.consideredgames.server.db

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

object Indexes extends LazyLogging {

  val indexFailureMsg = "Indexes failed to initialise"

  def initialise()(implicit ec: ExecutionContext): Future[Any] =
    for {
      _ <- UserDao.indexes.toFuture()
      f <- SessionDao.indexes.toFuture()
    } yield f

  def handleFailure(action: => Any)(e: Throwable): Unit = {
    logger.error(indexFailureMsg, e)
    action
  }
}
