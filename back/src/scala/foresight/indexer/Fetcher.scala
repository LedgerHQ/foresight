package foresight.indexer

import akka._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Flow
import common.Env
import common.model._
import foresight.model._
import scala.concurrent._
import scala.util._
import spray.json._
import spray.json.DefaultJsonProtocol._

final case class Fetcher(config: Fetcher.Config)(implicit system: ActorSystem) {

  implicit val ec = system.dispatcher

  val http = Http()

  val nodeConnection =
    http.cachedHostConnectionPool[NotUsed](config.host, config.port)

  def getBlockRequest(block: Height): HttpRequest =
    HttpRequest(uri = s"${config.path}/blocks/${block.value}")

  def getBlockHeadRequest: HttpRequest =
    HttpRequest(uri =
      s"http://${config.host}:${config.port}${config.path}/blocks/head"
    )

  def decodeBlock(res: HttpResponse): Future[Raw.Block] =
    Unmarshal(res)
      .to[JsObject]
      .map(Raw.Block.fromJson)
      .flatMap(Future.fromTry)

  def download =
    Flow[Height]
      .map(h => getBlockRequest(h) -> NotUsed)
      .via(nodeConnection)
      .mapAsync(config.concurrency) {
        case (Success(res), _) => decodeBlock(res)
        case (Failure(err), _) => Future.failed(err)
      }

  def head =
    http.singleRequest(getBlockHeadRequest).flatMap(decodeBlock)

}

object Fetcher {

  final case class Config(
      host: String,
      port: Int,
      path: String,
      concurrency: Int
  )

  object Config {
    def fromEnv: Config = {
      val host        = Env.getString("DOT_CLIENT_HOST", "localhost")
      val port        = Env.getInt("DOT_CLIENT_PORT", 80)
      val path        = Env.getString("DOT_CLIENT_ROOT", "")
      val concurrency = Env.getInt("DOT_CLIENT_CONCURRENCY")
      Config(host, port, path, concurrency)
    }
  }

}
