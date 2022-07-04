package foresight.indexer.server

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.model.ws.TextMessage
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.util.ByteString
import scala.concurrent.duration.DurationInt

class WsServer()(implicit system: ActorSystem) {

  val greeterWebSocketService =
    Flow.fromSinkAndSource(
      Sink.ignore,
      Source.tick(50.millis, 1.seconds, ()).map(_ => TextMessage("toto"))
    )

  val route = path("toto") {
    Directives.get {
      handleWebSocketMessages(greeterWebSocketService)
    }
  }
  val wsServer = Http()
    .newServerAt("127.0.0.1", 8081)
    .adaptSettings(
      _.mapWebsocketSettings(
        _.withPeriodicKeepAliveData(() => ByteString("toto"))
      )
    )
    .bind(route)

}
