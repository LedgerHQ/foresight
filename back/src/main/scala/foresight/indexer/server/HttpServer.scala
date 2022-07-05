package foresight.indexer.server

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.coding.Deflate
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.StatusCodes.MovedPermanently
import akka.http.scaladsl.model.ws.TextMessage
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Directives.handleWebSocketMessages
import akka.http.scaladsl.server.Directives.path
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.util.ByteString
import akka.util.Timeout
import foresight.indexer.RawInserter
import foresight.model.JsonProtocol._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.util.Success

class HttpServer(
    rawInserter: RawInserter
)(implicit system: ActorSystem) {

  val route = pathPrefix("processed-transactions" / IntNumber) { blockHeight =>
    onComplete(
      rawInserter
        .getProcessedTransactionByBockHeight(blockHeight)
    ) {
      case Success(value) => complete(value)
      case Failure(ex)    => complete(s"An error occurred: ${ex.getMessage}")
    }

  }

  val httpServer = Http()
    .newServerAt("0.0.0.0", 8080)
    .bind(route)

}
