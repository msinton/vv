package com.consideredgames.connect.action.handlers

import com.consideredgames.common.DateUtils
import com.consideredgames.connect.{ConnectionProtocols, _}
import com.consideredgames.game.state.Connectivity
import diode._
import diode.data._

import scala.concurrent.ExecutionContext

/**
  * Created by matt on 14/11/17.
  */
class ConnectionHandler[M](protocols: ConnectionProtocols)(modelRW: ModelRW[M, Pot[Connectivity]])
                          (implicit val ec: ExecutionContext)
  extends ActionHandler(modelRW) {

  def handle = {
    case Disconnected => updated(Unavailable)

    case ConnectAction(request) =>
      updated(value.pending(),
        Effect(protocols.connect(request)
          .map(ConnectSuccess)
          .recover { case e => ConnectFailed(e) }))

    case ConnectSuccess(_) => updated(value.ready(Connectivity(DateUtils.nowAsMillis)))

    case ConnectFailed(ex) => updated(value.fail(ex))
  }

}


