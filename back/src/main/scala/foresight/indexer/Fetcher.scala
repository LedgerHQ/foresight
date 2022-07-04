package foresight.indexer

import akka._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.ws._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl._
import common.Env
import common.model._
import foresight.model._
import scala.concurrent._
import scala.util._
import spray.json._
import spray.json.DefaultJsonProtocol._

final case class Fetcher(config: Fetcher.Config)(implicit system: ActorSystem) {

  implicit val ec = system.dispatcher

  private val wssRequest = WebSocketRequest(config.wsEndpoint)

  val http = Http()

  val nodeConnection =
    http.cachedHostConnectionPool[NotUsed](config.httpEndpoint)

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
      .map(_.asTextMessage.getStrictText.parseJson.asJsObject)
      .drop(1) // Subscription ACK not used
      .map(JRPC.Subscription.decode)
  }

  def newPendingTransactions =
    subscribe("newPendingTransactions")
      .collect { case JRPC.Subscription.Response(_, JsString(str)) =>
        str
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

    Flow[String]
      .map { hash => req(hash) -> NotUsed }
      .via(nodeConnection)
      .mapAsync(100) {
        case (Success(res), _) => decode(res)
        case (Failure(err), _) => Future.failed(err)
      }
      .filterNot(_.result == JsNull)
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
