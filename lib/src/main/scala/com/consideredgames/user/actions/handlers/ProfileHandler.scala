package com.consideredgames.user.actions.handlers

import com.consideredgames.user.Profile
import com.consideredgames.user.actions.User
import diode.{ActionHandler, ActionResult, ModelRW}

/**
  * Created by matt on 25/11/17.
  */
class ProfileHandler[M](modelRW: ModelRW[M, Option[Profile]]) extends ActionHandler(modelRW) {

  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case User(username) => updated(Option(Profile(username)))
  }
}
