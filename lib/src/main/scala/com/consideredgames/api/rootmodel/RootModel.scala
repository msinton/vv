package com.consideredgames.api.rootmodel

import com.consideredgames.game.state.Connectivity
import diode.data.Pot

/**
  * Created by matt on 19/11/17.
  */
case class RootModel(
      connectivity: Pot[Connectivity] = Pot.empty,
      messages: Messages = Messages()
  )
