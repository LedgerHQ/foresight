package foresight.indexer

import akka._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.ws._
import akka.http.scaladsl.model.ws.TextMessage.Streamed
import akka.http.scaladsl.model.ws.TextMessage.Strict
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl._
import common.Env
import common.model._
import foresight.model._
import java.sql.Timestamp
import scala.concurrent._
import scala.concurrent.duration._
import scala.util._
import spray.json._
import spray.json.DefaultJsonProtocol._

final case class Fetcher(config: Fetcher.Config)(implicit system: ActorSystem) {

  implicit val ec = system.dispatcher

  private val wssRequest = WebSocketRequest(config.wsEndpoint)

  val http = Http()

  val nodeConnection =
    http.cachedHostConnectionPool[Timestamp](config.httpEndpoint)

  val messageToJson =
    Flow[Message]
      .collectType[TextMessage]
      .mapAsync(10) {
        case Streamed(stream) =>
          stream.runFold(new StringBuilder)(_ append _).map(_.toString())
        case Strict(text) =>
          Future.successful(text)
      }
      .map(_.parseJson.asJsObject)

  def subscribe(topic: String) = {
    val req = JRPC.Request(
      id = 0,
      method = "eth_subscribe",
      params = Vector(JsString(topic))
    )

    val wssFlow = http.webSocketClientFlow(wssRequest)

    Source
      .single(TextMessage(req.encode.toString))
      .concat(Source.maybe)
      .viaMat(wssFlow)(Keep.right)
      .via(messageToJson)
      .drop(1) // Subscription ACK not used
      .map(JRPC.Subscription.decode)
  }

  def newPendingTransactions =
    subscribe("newPendingTransactions")
      .collect { case JRPC.Subscription.Response(_, JsString(str)) =>
        (str, Raw.sqlTimestampNow)
      }

  def newHeads = {
    import foresight.model.JsonProtocol._
    subscribe("newHeads")
      .collect { case JRPC.Subscription.Response(_, json: JsObject) =>
        (json.convertTo[ClientHead].hash, Raw.sqlTimestampNow)
      }
  }

  def baseFees = {
    import foresight.model.JsonProtocol._
    Source
      .tick(50.millis, 15.seconds, ())
      .via(getFeeHistory)
      .collect { case JRPC.Response(_, json: JsObject) =>
        json.convertTo[ClientFeeHistory]
      }
  }

  def getBlock = {

    def req(hash: String) = {
      val jrpcRequest = JRPC
        .Request(
          id = 0,
          method = "eth_getBlockByHash",
          params = Vector(JsString(hash), JsBoolean(true))
        )

      HttpRequest()
        .withMethod(HttpMethods.POST)
        .withHeaders(Authorization(BasicHttpCredentials(config.httpBasicAuth)))
        .withEntity(
          HttpEntity(
            ContentTypes.`application/json`,
            jrpcRequest.encode.toString
          )
        )

    }

    def decode(res: HttpResponse): Future[JRPC.Response] =
      Unmarshal(res)
        .to[JsObject]
        .map(JRPC.Response.decode)
        .flatMap(res => Future.fromTry(Try(res)))

    Flow[(String, Timestamp)]
      .map { case (hash, ts) => req(hash) -> ts }
      .via(nodeConnection)
      .mapAsync(10) {
        case (Success(res), ts) => decode(res).map(_ -> ts)
        case (Failure(err), ts) => Future.failed(err)
      }
      .filterNot { case (x, _) => x.result == JsNull }
  }

  def getTx = {

    def req(hash: String) = {
      val jrpcRequest = JRPC
        .Request(
          id = 0,
          method = "eth_getTransactionByHash",
          params = Vector(JsString(hash))
        )

      HttpRequest()
        .withMethod(HttpMethods.POST)
        .withHeaders(Authorization(BasicHttpCredentials(config.httpBasicAuth)))
        .withEntity(
          HttpEntity(
            ContentTypes.`application/json`,
            jrpcRequest.encode.toString
          )
        )

    }

    def decode(res: HttpResponse): Future[JRPC.Response] =
      Unmarshal(res)
        .to[JsObject]
        .map(JRPC.Response.decode)
        .flatMap(res => Future.fromTry(Try(res)))

    Flow[(String, Timestamp)]
      .map { case (hash, ts) => req(hash) -> ts }
      .via(nodeConnection)
      .mapAsync(10) {
        case (Success(res), ts) => decode(res).map(_ -> ts)
        case (Failure(err), ts) => Future.failed(err)
      }
      .filterNot { case (x, _) => x.result == JsNull }
  }

  def getFeeHistory = {
    val co = http.cachedHostConnectionPool[NotUsed](config.httpEndpoint)

    val jrpcRequest = JRPC
      .Request(
        id = 0,
        method = "eth_feeHistory",
        params = Vector(
          JsString("0xF"),
          JsString("latest"),
          JsArray(Vector.empty)
        )
      )

    val httpRequest = HttpRequest()
      .withMethod(HttpMethods.POST)
      .withHeaders(Authorization(BasicHttpCredentials(config.httpBasicAuth)))
      .withEntity(
        HttpEntity(
          ContentTypes.`application/json`,
          jrpcRequest.encode.toString
        )
      )

    def decode(res: HttpResponse): Future[JRPC.Response] =
      Unmarshal(res)
        .to[JsObject]
        .map(JRPC.Response.decode)
        .flatMap(res => Future.fromTry(Try(res)))

    Flow[Unit]
      .map { case _ => httpRequest -> NotUsed }
      .via(co)
      .mapAsync(10) {
        case (Success(res), ts) => decode(res).map(_ -> ts)
        case (Failure(err), ts) => Future.failed(err)
      }
      .map { case (x, _) => x }
  }

}

object Fetcher {

  final case class Config(
      wsEndpoint: String,
      httpEndpoint: String,
      httpBasicAuth: String
  )

  object Config {
    def fromEnv: Config = {
      val wsEndpoint    = Env.getString("CLIENT_WS_ENDPOINT")
      val httpEndpoint  = Env.getString("CLIENT_HTTP_ENDPOINT")
      val httpBasicAuth = Env.getString("CLIENT_HTTP_BASIC_AUTH")
      Config(wsEndpoint, httpEndpoint, httpBasicAuth)
    }
  }

}
