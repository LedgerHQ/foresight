package foresight.indexer

import akka._
import akka.actor.ActorSystem
import akka.stream.OverflowStrategy
import akka.stream.alpakka.slick.scaladsl.SlickSession
import akka.stream.scaladsl._
import akka.util.ByteString
import common.indexer._
import common.model.JRPC
import foresight.indexer._
import foresight.indexer.server.WsServer
import foresight.model.Raw
import java.sql.Timestamp
import java.time.Instant
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util._
import slick.jdbc.PositionedParameters
import slick.jdbc.SetParameter
import spray.json._

object Indexer {

  def main(args: Array[String]): Unit = {
    val dbConfig      = DB.Config.fromEnv
    val fetcherConfig = Fetcher.Config.fromEnv

    implicit val system: ActorSystem = ActorSystem()

    implicit val session = DB.session(dbConfig)
    system.registerOnTermination(session.close())
    // Await.result(DB.initSchema(session, "schema.sql"), 30.seconds)

    val rawInserter = RawInserter(session)

    val fetcher = Fetcher(fetcherConfig)

    fetcher.baseFees
      .map(Raw.BaseFeeBatch.fromClient)
      .via(rawInserter.updateBaseFee)
      .log("Base fee")
      .toMat(Sink.ignore)(Keep.both)
      .run()

    fetcher.newHeads
      .via(fetcher.getBlock)
      .map { case (x, ts) =>
        Raw.Block.fromJson(x.result.asJsObject, ts)
      }
      .collect { case Success(block) => block }
      .via(rawInserter.insertBlock)
      .log("new blocks")
      .toMat(Sink.ignore)(Keep.both)
      .run()

    fetcher.newPendingTransactions
      .via(fetcher.getTx)
      .map { case (x, ts) =>
        Raw.PendingTransaction.fromJson(x.result.asJsObject, ts)
      }
      .collect { case Success(pending) =>
        pending
      }
      .via(rawInserter.insertTransaction)
      .log("new pending txs")
      .toMat(Sink.ignore)(Keep.both)
      .run()

    new WsServer(rawInserter).wsServer
  }
}
