package com.consideredgames.api.rootmodel

import com.consideredgames.activity.state.Activity
import com.consideredgames.connect.state.{Connectivity, Messages}
import com.consideredgames.user.Profile
import diode.data.Pot

/**
  * Created by matt on 19/11/17.
  */
case class RootModel(
      connectivity: Pot[Connectivity] = Pot.empty,
      messages: Messages = Messages(),
      profile: Option[Profile] = None,
      activity: Activity = Activity()
  )
