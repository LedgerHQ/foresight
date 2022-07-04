package foresight.indexer

import akka._
import akka.actor.ActorSystem
import akka.stream.OverflowStrategy
import akka.stream.alpakka.slick.scaladsl.SlickSession
import akka.stream.scaladsl._
import akka.util.ByteString
import common.indexer._
import foresight.indexer._
import foresight.indexer.server.WsServer
import scala.concurrent._
import scala.concurrent.duration._
import scala.util._
import slick.jdbc.PositionedParameters
import slick.jdbc.SetParameter
import spray.json._

object Indexer {

  def main(args: Array[String]): Unit = {
    val dbConfig      = DB.Config.fromEnv
    val fetcherConfig = Fetcher.Config.fromEnv
    // val dbConfig =
    //  DB.Config("localhost", 5432, "foresight", "username", "password")
    // val fetcherConfig = Fetcher.Config(
    //  endpoint = "51.210.220.222",
    //  concurrency = 40
    // )

    implicit val system = ActorSystem()

    new WsServer().wsServer
    try {
      implicit val session = DB.session(dbConfig)
      system.registerOnTermination(session.close())
      Await.result(DB.initSchema(session, "schema.sql"), 30.seconds)

      val rawInserter = RawInserter(session)

      val fetcher = Fetcher(fetcherConfig)

      val steps = List(
        DownloadStep(fetcher, rawInserter)
      )

      val stream = Source
        .tick(50.millis, 5.seconds, ())
        .buffer(1, OverflowStrategy.backpressure)
        .mapAsync(1) { _ =>
          steps.foldLeft(Future.successful(Done.done())) { (prev, step) =>
            prev.flatMap(_ => step.run)(system.dispatcher)
          }
        }

      Await.result(stream.run(), Duration.Inf)
    } catch {
      case e: Throwable =>
        system.log.error(e.getMessage())
    } finally {
      Await.result(system.terminate(), 3.seconds)
    }

  }
}
