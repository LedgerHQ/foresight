package foresight.indexer.server

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.model.ws.TextMessage
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.util.ByteString
import foresight.indexer.RawInserter
import foresight.model.JsonProtocol._
import foresight.model.Processed
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import spray.json._

class WsServer(
    rawInserter: RawInserter
)(implicit system: ActorSystem) {

  val webSocketService =
    Flow.fromSinkAndSource(
      Sink.ignore,
      Source
        .tick(50.millis, 1.seconds, ())
        .mapAsync(4)(_ =>
          rawInserter.getProcessedTransaction.map(t =>
            TextMessage(t.toJson.toString)
          )
        )
    )

  val route = path("processed-transactions") {
    Directives.get {
      handleWebSocketMessages(webSocketService)
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
