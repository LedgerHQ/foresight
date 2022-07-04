package foresight.indexer

import akka.Done
import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import common.indexer._
import common.model._
import foresight.indexer.RawInserter
import foresight.model._
import scala.concurrent._

final case class DownloadStep(
    fetcher: Fetcher,
    rawInserter: RawInserter
)(implicit system: ActorSystem)
    extends Step {

  implicit val ec = system.dispatcher

  override def name: String = "Download Blocks"

  override def size: Int = 1000

  override def reached: Future[Height] =
    rawInserter.topHeight.map(_.getOrElse(Height.genesis))

  override def target: Future[Height] =
    fetcher.head.map(_.height)

  override def index(from: Height, to: Height): Future[Done] =
    Source((from.value to to.value).map(Height.apply))
      .via(fetcher.download)
      .via(rawInserter.insertBlock)
      .run()

}
