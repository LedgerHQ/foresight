package common.indexer

import akka.Done
import akka.actor.ActorSystem
import common.model._
import org.slf4j.LoggerFactory
import scala.concurrent._

trait Step {
  def name: String
  def size: Int
  def reached: Future[Height]
  def target: Future[Height]

  def index(from: Height, to: Height): Future[Done]

  protected val logger = LoggerFactory.getLogger(s"[$name]")

  def run(implicit system: ActorSystem): Future[Done] = {
    implicit val ec = system.dispatcher
    for {
      lbMin <- reached
      ubMax <- target

      lb = Height(lbMin.value + 1)
      ub = Height(lb.value + Math.min(ubMax.value - lb.value, size))

      _ = logger.info(s"Plan : from $lb to $ub (max is $ubMax)")
      _ <- index(lb, ub)
      _ = logger.info(s"Done.")
    } yield Done
  }
}
